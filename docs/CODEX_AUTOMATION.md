# Codex automation handoff

DesktopPet supports two Codex handoff paths.

## Preferred path: push a `codex/*` branch

A Codex task should:

1. work on a branch whose name starts with `codex/`;
2. implement only the authorized roadmap milestone(s);
3. validate locally where possible;
4. make a final commit whose message contains exactly one standalone automation marker:
   - `Automation: auto-merge`, or
   - `Automation: manual-review`;
5. push the branch to this repository.

After the push, repository automation takes over:

1. `Validate Codex branches` runs on `windows-latest` with read-only repository permission;
2. it runs `mvn --batch-mode clean verify`, verifies DesktopPet packaging outputs, and uploads the distribution artifact;
3. only after that exact SHA succeeds, `Promote validated Codex branches` runs from the default branch with write permission;
4. the promotion workflow creates a pull request if one does not already exist;
5. `Automation: auto-merge` PRs may be squash-merged automatically at the exact validated SHA;
6. `Automation: manual-review` PRs remain open for the required human smoke check and manual merge.

The write-capable promotion job never checks out or executes Codex branch code. If the
Codex branch changes the branch-validator workflow or any `.github/workflows/` file, the
promotion is forced to manual review.

## One-time Codex Cloud setup

Repository automation cannot push a branch out of the Codex sandbox by itself. Give the
Codex environment a repository-scoped GitHub credential once, then use the checked-in
setup helper.

Recommended credential:

- fine-grained personal access token;
- restricted to `8skyness8/DektopPet` only;
- minimum repository permission needed for this handoff: **Contents: Read and write**;
- do not grant administration, secrets, or organization permissions.

Store the token in the Codex environment as the secret `DESKTOPPET_GITHUB_TOKEN`. Do not
paste the token into prompts, source files, commits, or GitHub Issues.

Configure the Codex environment setup command to run:

```bash
bash scripts/codex/setup-github.sh
```

The helper authenticates GitHub CLI without printing the token, configures Git's GitHub
credential helper, and makes sure `origin` points at this repository.

## Main-branch protection

Before giving Codex a token with Contents write access, protect `main` in GitHub so work
must arrive through a pull request. Recommended rules are:

- target branch: `main`;
- require a pull request before merging;
- prevent force pushes and branch deletion;
- require the `build` status check once GitHub exposes it for this repository;
- do not require an approving reviewer for this single-developer repository unless desired.

The Codex token is for pushing `codex/*` branches, not for pushing `main` directly.

## Fallback path

If the Codex environment has no usable GitHub credential, the task should still commit
its changes and report that branch push failed. The user can then use the Codex UI's
Create PR handoff or another authenticated Git client. Existing `Validate pull requests`
and `Auto merge validated Codex pull requests` workflows continue to support manually
created PRs.
