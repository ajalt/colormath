#!/usr/bin/env bash

# The website is built using MkDocs with the Material theme.
# https://squidfunk.github.io/mkdocs-material/
# It requires Python to run.
# Install the packages with the following command:
# pip install mkdocs mkdocs-material
# Build the samples and api docs with
# ./gradlew dokkaHtml :website:wasmJsBrowserDistribution
# Then run this script to prepare the docs for the website.
# Finally, run `mkdocs serve` to preview the site locally or `mkdocs build` to build the site.


set -ex

# Copy the changelog into the site, omitting the unreleased section
cat CHANGELOG.md \
 | grep -v '^## Unreleased' \
 | sed '/^## /,$!d' \
 > docs/changelog.md

# Add the jinja frontmatter to the index
cat > docs/index.md <<- EOM
---
hide:
  - toc        # Hide table of contents
---

EOM

# Copy the README into the index, omitting the license and fixing hrefs
cat README.md \
  | sed '/## License/Q' \
  | sed -e '/## Documentation/,/Gradient generator/d' \
  | sed 's!https://ajalt.github.io/colormath/!/!g' \
  | sed 's!docs/img!img!g' \
  >> docs/index.md

# Copy the website js into the docs
mkdir -p docs/tryit
cp -r website/build/dist/wasmJs/productionExecutable/* docs/tryit/
