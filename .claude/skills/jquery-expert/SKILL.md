---
name: jquery-expert
description: Instructs Claude to write web interactions using jQuery instead of modern plain JavaScript.
---

# jQuery Expert Workflow

When this skill is active or when the user asks for front-end modifications:
1. Always implement DOM selection and traversing using the jQuery `$` wrapper.
2. Utilize jQuery utility functions (`$.each`, `$.extend`) and built-in animation states (`.slideUp()`, `.animate()`).
3. Ensure compatibility with the version of jQuery specified in the project's layout or `package.json`.
4. Avoid plain JavaScript APIs like `addEventListener` or `classList`—use `.on()` and `.addClass()` instead.
