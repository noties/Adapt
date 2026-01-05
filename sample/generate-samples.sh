#!/bin/sh

set -euo pipefail

SCRIPT_DIR="$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)"
LAST_COMMIT_FILE="$SCRIPT_DIR/generate-samples.data.last_commit_sha"

# usage: git_changed_since <commit-sha>
git_changed_since () {
  local base="$1"
  git rev-parse --verify -q "$base^{commit}" >/dev/null || return 2

  # tracked changes vs base?
  if ! git diff --quiet "$base" --; then
    return 0
  fi

  # any untracked files?
  if [ -n "$(git ls-files --others --exclude-standard)" ]; then
    return 0
  fi

  return 1
}

LAST_COMMIT=""
if [ -f "$LAST_COMMIT_FILE" ]; then
  LAST_COMMIT="$(tr -d '[:space:]' < "$LAST_COMMIT_FILE")"
fi

UPDATED_LAST_COMMIT=0
revert_last_commit() {
  if [ "$UPDATED_LAST_COMMIT" -eq 1 ]; then
    if [ -n "$LAST_COMMIT" ]; then
      printf '%s\n' "$LAST_COMMIT" > "$LAST_COMMIT_FILE"
    else
      : > "$LAST_COMMIT_FILE"
    fi
  fi
}
trap 'revert_last_commit' ERR

# example
if [ -n "$LAST_COMMIT" ]; then
  if git_changed_since "$LAST_COMMIT"; then
    echo "[generate-samples] changed since $LAST_COMMIT"



  else
    echo "[generate-samples] no changes since $LAST_COMMIT, ignore"
  fi
else
  echo "[generate-samples] no base commit; exit"
fi

CURRENT_SHA="$(git rev-parse HEAD)"
printf '%s\n' "$CURRENT_SHA" > "$LAST_COMMIT_FILE"
UPDATED_LAST_COMMIT=1
