# BShoes — Standalone Vue Preview

No-build preview of the UI. Open `preview/index.html` directly in a browser
(double-click, or serve the folder with any static server). Requires internet
access for the Bootstrap and Vue CDN scripts.

These pages mirror the Spring Boot Thymeleaf templates in
`src/main/resources/templates/` but use a plain shared JS shell
(`assets/shell.js`) instead of the Thymeleaf layout fragment, so they run
without the backend. Mock data only.
