#!/bin/bash
set -e
artifact_id=reversi

for username in "$@"; do
  echo "Fetching $username's fork"
  git fetch git@git.cis.uab.edu:$username/$artifact_id.git
  git diff ...FETCH_HEAD


  read -p "Hit enter when ready to merge $username's fork"
  git stash
  git merge --no-commit --squash FETCH_HEAD
  git stash pop
done

