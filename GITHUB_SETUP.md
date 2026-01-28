# GitHub Setup Guide

## Prerequisites

### Install Git for Windows
1. Download from: https://git-scm.com/download/win
2. Run installer and use default settings
3. Restart PowerShell/Command Prompt after installation

## Repository Setup

Your GitHub repository: **https://github.com/04162214027/MUNDO**

### Step 1: Initialize Git Repository
```bash
cd "c:\Users\WAQAR\Desktop\app\MobileShopERP"
git init
```

### Step 2: Configure Git (First Time Only)
```bash
git config --global user.name "YOUR_NAME"
git config --global user.email "your.email@example.com"
```

### Step 3: Add Remote Repository
```bash
git remote add origin https://github.com/04162214027/MUNDO.git
```

### Step 4: Add All Files
```bash
git add .
```

### Step 5: Create Initial Commit
```bash
git commit -m "Initial commit - Mobile Shop ERP Android App"
```

### Step 6: Push to GitHub
```bash
git branch -M main
git push -u origin main
```

## Alternative: GitHub Desktop (Easier)

If you prefer a GUI:

1. **Download GitHub Desktop**: https://desktop.github.com/
2. **Install** and **Sign in** with your GitHub account
3. **Add existing repository**:
   - File → Add Local Repository
   - Choose: `c:\Users\WAQAR\Desktop\app\MobileShopERP`
4. **Commit changes**:
   - Check all files in left panel
   - Add commit message: "Initial commit"
   - Click "Commit to main"
5. **Publish repository**:
   - Click "Publish repository"
   - Repository name: MUNDO
   - Click "Publish Repository"

## After Pushing to GitHub

### Automatic APK Building

Once pushed, GitHub Actions will automatically:
1. Build debug APK on every push
2. Run tests
3. Upload APK as artifact

### Download Built APK

1. Go to: https://github.com/04162214027/MUNDO/actions
2. Click on the latest workflow run
3. Scroll to **Artifacts** section
4. Download `app-debug` (APK will be inside)

### Create Release

To trigger a release build:
```bash
git tag v1.0.0
git push origin v1.0.0
```

This will:
- Build release APK
- Create GitHub Release
- Attach APK to release

## Useful Commands

### Check Status
```bash
git status
```

### View Changes
```bash
git diff
```

### Push Changes
```bash
git add .
git commit -m "Your commit message"
git push
```

### Pull Latest Changes
```bash
git pull
```

### View Remote URL
```bash
git remote -v
```

## Troubleshooting

### Authentication Issues
If GitHub asks for credentials, use **Personal Access Token** instead of password:
1. Go to: https://github.com/settings/tokens
2. Generate new token (classic)
3. Select scopes: `repo`, `workflow`
4. Copy token and use as password

### Large Files
If you get errors about large files, add them to `.gitignore`:
```
app/build/
*.apk
*.aab
```

## Ready to Push!

Once Git is installed, run:
```bash
cd "c:\Users\WAQAR\Desktop\app\MobileShopERP"
git init
git add .
git commit -m "Initial commit"
git remote add origin https://github.com/04162214027/MUNDO.git
git branch -M main
git push -u origin main
```

Your APK will be built automatically by GitHub Actions! 🚀
