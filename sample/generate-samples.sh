#!/bin/sh

set -euo pipefail

SCRIPT_DIR="$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)"
LAST_COMMIT_FILE="$SCRIPT_DIR/generate-samples.data.last_commit_sha"
WATCH_DIR_REL="sample/src/main/java/io/noties/adapt/sample/samples"

REPO_ROOT="$(git rev-parse --show-toplevel)"
WATCH_DIR="$REPO_ROOT/$WATCH_DIR_REL"

# usage: git_changed_since <commit-sha> <path>
git_changed_since () {
  local base="$1"
  local watch_path="$2"
  git rev-parse --verify -q "$base^{commit}" >/dev/null || return 2

  # tracked changes vs base?
  if ! git diff --quiet "$base" -- "$watch_path"; then
    return 0
  fi

  # any untracked files?
  if [ -n "$(git ls-files --others --exclude-standard -- "$watch_path")" ]; then
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
  if git_changed_since "$LAST_COMMIT" "$WATCH_DIR"; then
    echo "[generate-samples] changed since $LAST_COMMIT in $WATCH_DIR_REL"

    (
        cd '../sample-utils/processor2'
        ./processor.sh '../../sample/src/main/java/io/noties/adapt/sample/samples' \
            --tags-source='../../sample/src/main/java/io/noties/adapt/sample/samples/Tags.kt' \
            --output-file '../../sample/samples.json'
    )

  else
    echo "[generate-samples] no changes since $LAST_COMMIT in $WATCH_DIR_REL, ignore"
  fi
else
  echo "[generate-samples] no base commit; exit"
  exit 0;
fi

CURRENT_SHA="$(git rev-parse HEAD)"
printf '%s\n' "$CURRENT_SHA" > "$LAST_COMMIT_FILE"
UPDATED_LAST_COMMIT=1
