#!/usr/bin/env bash
set -euo pipefail

repository="${DESKTOPPET_GITHUB_REPOSITORY:-8skyness8/DektopPet}"
token="${DESKTOPPET_GITHUB_TOKEN:-}"

if [[ -z "$token" ]]; then
  echo "DESKTOPPET_GITHUB_TOKEN is not set." >&2
  echo "Add a repository-scoped fine-grained GitHub token to the Codex environment secrets." >&2
  exit 1
fi

if ! command -v gh >/dev/null 2>&1; then
  if command -v apt-get >/dev/null 2>&1; then
    sudo apt-get update -qq
    sudo apt-get install -y -qq gh
  else
    echo "GitHub CLI (gh) is required and could not be installed automatically." >&2
    exit 1
  fi
fi

# Authenticate without printing the token, then let gh configure Git's credential helper.
printf '%s\n' "$token" | gh auth login --hostname github.com --with-token >/dev/null
gh auth setup-git >/dev/null

origin_url="https://github.com/${repository}.git"
if git remote get-url origin >/dev/null 2>&1; then
  git remote set-url origin "$origin_url"
else
  git remote add origin "$origin_url"
fi

echo "GitHub push handoff configured for $repository."
echo "Codex should push only codex/* branches; repository automation creates/validates the PR."
