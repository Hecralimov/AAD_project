# Branching Strategy & Git Workflow Guide

When multiple people are working simultaneously on a project, especially aiming towards a specific deployment goal like `deploy/V7`, it's easy for the repository to accumulate a lot of branches. This document provides a simplified strategy to keep the repository clean, avoid merge conflicts, and collaborate efficiently.

## 1. Branch Naming Conventions

To keep track of what each branch does, use standard prefixes:
* **`feature/<name>`**: For new features (e.g., `feature/shopping-cart`, `feature/admin-login`).
* **`bugfix/<name>`** or **`fix/<name>`**: For bug fixes (e.g., `fix/login-page-error`).
* **`deploy/<version>`**: For integration branches targeting a specific deployment (e.g., `deploy/V7`).
* **`main`**: The stable, production-ready codebase.

*Avoid using vague names like `feature/loginpageingetirdigibozukolaylar` or tracking arbitrary task lists like `deploy/A3` unless absolutely necessary.*

## 2. Collaborating on `deploy/V7`

Since two people are working on the `deploy/V7` target, you should treat `deploy/V7` as an **Integration Branch**, rather than directly committing to it simultaneously.

**Workflow:**
1. **Pull the latest changes:** Start by pulling the latest `deploy/V7`.
   ```bash
   git checkout deploy/V7
   git pull origin deploy/V7
   ```
2. **Create a feature branch:** Create a new branch *off* of `deploy/V7` for your specific task.
   ```bash
   git checkout -b feature/your-specific-task
   ```
3. **Commit your work:** Make small, frequent commits with clear messages.
4. **Sync with the integration branch:** Before merging your feature back, pull any new changes that your teammate might have added to `deploy/V7` in the meantime.
   ```bash
   git checkout deploy/V7
   git pull origin deploy/V7
   git checkout feature/your-specific-task
   git merge deploy/V7
   # Resolve any conflicts here, in your feature branch!
   ```
5. **Merge back:** Once your feature branch is up-to-date and works correctly, merge it into `deploy/V7` (preferably via a Pull Request if you are using GitHub/GitLab).
   ```bash
   git checkout deploy/V7
   git merge feature/your-specific-task
   git push origin deploy/V7
   ```

## 3. Dealing with Clutter: Cleaning Up Old Branches

Currently, the repository has many leftover branches (e.g., `deploy/A3`, `deploy/b4`, `fix/priority1`).

**How to clean up:**
1. **Identify merged branches:** Check which branches have already been merged into `main` or `deploy/V7`.
   ```bash
   git branch --merged
   ```
2. **Delete local branches:**
   ```bash
   git branch -d <branch-name>
   ```
   *(Use `-D` if you want to force delete an unmerged branch that you know you don't need anymore).*
3. **Delete remote branches:** If the branch is on the remote server (e.g., GitHub) and is no longer needed:
   ```bash
   git push origin --delete <branch-name>
   ```

*Tip: Make it a habit to delete your feature branch immediately after it has been successfully merged.*

## 4. Preventing Merge Conflicts
* **Communicate:** Talk to each other about which files you are working on. Try to avoid working on the exact same file at the exact same time.
* **Pull frequently:** Run `git pull origin deploy/V7` often to keep your local environment up to date with your teammate's work.
* **Keep branches short-lived:** Do not keep a feature branch open for weeks. The longer a branch lives, the harder it is to merge.
