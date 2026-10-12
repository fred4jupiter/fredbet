#!/usr/bin/env python3
"""
Publishes the PMD report (target/pmd.xml) on the GitHub Actions build page:
- an annotation per violation (shown in the run and on the changed files)
- a job summary with the number of issues per rule and the full list of issues

The script never fails the build: it always exits with 0.
"""
import collections
import html
import os
import sys
import xml.etree.ElementTree as ET

NS = {"pmd": "http://pmd.sourceforge.net/report/2.0.0"}
MAX_SUMMARY_ROWS = 500


def relative_path(path, workspace):
    if workspace and path.startswith(workspace):
        return os.path.relpath(path, workspace)
    return path


def escape_annotation(value, is_property=False):
    # see https://github.com/actions/toolkit/blob/main/packages/core/src/command.ts
    value = value.replace("%", "%25").replace("\r", "%0D").replace("\n", "%0A")
    if is_property:
        value = value.replace(":", "%3A").replace(",", "%2C")
    return value


def md_cell(value):
    return html.escape(value, quote=False).replace("|", "\\|").replace("\n", " ")


def read_violations(report_file, workspace):
    root = ET.parse(report_file).getroot()
    violations = []
    for file_element in root.findall("pmd:file", NS):
        path = relative_path(file_element.get("name"), workspace)
        for v in file_element.findall("pmd:violation", NS):
            violations.append({
                "file": path,
                "line": int(v.get("beginline", "1")),
                "end_line": int(v.get("endline", v.get("beginline", "1"))),
                "rule": v.get("rule", ""),
                "ruleset": v.get("ruleset", ""),
                "priority": int(v.get("priority", "5")),
                "url": v.get("externalInfoUrl", ""),
                "message": (v.text or "").strip(),
            })
    errors = [(relative_path(e.get("filename", ""), workspace), e.get("msg", ""))
              for e in root.findall("pmd:error", NS)]
    return violations, errors


def print_annotations(violations):
    # priority 1 (high) and 2 are shown as warnings, the others as notices
    for v in sorted(violations, key=lambda x: (x["priority"], x["file"], x["line"])):
        level = "warning" if v["priority"] <= 2 else "notice"
        props = ",".join([
            "file=" + escape_annotation(v["file"], True),
            "line=%d" % v["line"],
            "endLine=%d" % v["end_line"],
            "title=" + escape_annotation("PMD %s (priority %d)" % (v["rule"], v["priority"]), True),
        ])
        print("::%s %s::%s" % (level, props, escape_annotation(v["message"])))


def write_summary(violations, errors, summary_file):
    server = os.environ.get("GITHUB_SERVER_URL", "https://github.com")
    repo = os.environ.get("GITHUB_REPOSITORY", "")
    sha = os.environ.get("GITHUB_SHA", "")

    lines = ["## Static code analysis (PMD)", ""]
    if not violations and not errors:
        lines.append(":white_check_mark: No issues found.")
    else:
        lines.append("Found **%d** issue(s) in **%d** file(s). The build is not affected by these findings."
                     % (len(violations), len({v["file"] for v in violations})))
        lines += ["", "### Issues per rule", "", "| Rule | Ruleset | Priority | Count |", "|---|---|---|---|"]
        per_rule = collections.Counter((v["rule"], v["ruleset"], v["priority"], v["url"]) for v in violations)
        for (rule, ruleset, priority, url), count in sorted(per_rule.items(), key=lambda x: (x[0][2], -x[1])):
            rule_cell = "[%s](%s)" % (md_cell(rule), url) if url else md_cell(rule)
            lines.append("| %s | %s | %d | %d |" % (rule_cell, md_cell(ruleset), priority, count))

        lines += ["", "<details><summary>All issues</summary>", "",
                  "| Priority | Location | Rule | Message |", "|---|---|---|---|"]
        ordered = sorted(violations, key=lambda x: (x["priority"], x["file"], x["line"]))
        for v in ordered[:MAX_SUMMARY_ROWS]:
            location = "%s:%d" % (v["file"], v["line"])
            if repo and sha:
                location = "[%s](%s/%s/blob/%s/%s#L%d)" % (md_cell(location), server, repo, sha, v["file"], v["line"])
            lines.append("| %d | %s | %s | %s |" % (v["priority"], location, md_cell(v["rule"]), md_cell(v["message"])))
        if len(ordered) > MAX_SUMMARY_ROWS:
            lines.append("")
            lines.append("_Only the first %d issues are listed. See the uploaded PMD report for all issues._" % MAX_SUMMARY_ROWS)
        lines += ["", "</details>"]

        if errors:
            lines += ["", "### Processing errors", ""]
            lines += ["- `%s`: %s" % (f, md_cell(msg)) for f, msg in errors]

    with open(summary_file, "a", encoding="utf-8") as f:
        f.write("\n".join(lines) + "\n")


def main():
    report_file = sys.argv[1] if len(sys.argv) > 1 else "target/pmd.xml"
    workspace = os.environ.get("GITHUB_WORKSPACE", os.getcwd())
    summary_file = os.environ.get("GITHUB_STEP_SUMMARY")

    if not os.path.isfile(report_file):
        print("::warning title=Static code analysis::PMD report %s not found." % report_file)
        return

    try:
        violations, errors = read_violations(report_file, workspace)
    except ET.ParseError as e:
        print("::warning title=Static code analysis::PMD report could not be parsed: %s" % escape_annotation(str(e)))
        return

    print("PMD found %d issue(s)." % len(violations))
    print_annotations(violations)
    if summary_file:
        write_summary(violations, errors, summary_file)


if __name__ == "__main__":
    try:
        main()
    except Exception as e:  # the analysis must never break the build
        print("::warning title=Static code analysis::Could not publish the PMD report: %s" % escape_annotation(str(e)))
    sys.exit(0)
