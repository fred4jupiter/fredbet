# CLAUDE.md

## Frontend JavaScript

Always use the `jquery-expert` skill when writing or changing frontend JavaScript. Use jQuery for DOM access, events and AJAX instead of plain DOM APIs.

- Use `$('#id')`, `.on()`, `.css()`, `.attr()` and the Bootstrap jQuery plugins (`.popover()`, `.tooltip()`).
- Don't use `document.getElementById`, `querySelectorAll`, `addEventListener`, `classList` or `new bootstrap.X(...)`.
- Follow the style of the existing scripts in `src/main/resources/static/js/pages/`: `"use strict";` plus a `$(function () {...})` wrapper.

## Tests

Use AssertJ (`org.assertj.core.api.Assertions.assertThat`) for assertions, not Hamcrest. Don't add new Hamcrest matchers or imports.

For web tests, inject `MockMvcTester` (not `MockMvc`) and assert with AssertJ, e.g. `assertThat(mvc.get().uri("/x")).hasStatusOk().bodyText().contains(...)`.
