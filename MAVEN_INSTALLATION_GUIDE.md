# 🔧 Maven Installation Guide for Windows

## ⚠️ Permission Issue with Chocolatey
Chocolatey installation failed due to permission issues. Let's install Maven manually.

## 🚀 Manual Maven Installation

### Step 1: Download Maven
1. Go to: https://maven.apache.org/download.cgi
2. Download **Binary zip archive**: `apache-maven-3.9.5-bin.zip` (or latest version)
3. Save it to your Downloads folder

### Step 2: Install Java (if not already installed)
```powershell
# Check Java installation
java -version

# If not installed, download from: https://adoptium.net/
# Or try: choco install openjdk11 (if Chocolatey permissions work)
```

### Step 3: Extract Maven
1. Create directory: `C:\apache-maven`
2. Extract the downloaded zip file to `C:\apache-maven`
3. You should have: `C:\apache-maven\apache-maven-3.9.5\`

### Step 4: Set Environment Variables

#### Method A: Using PowerShell (Run as Administrator)
```powershell
# Set MAVEN_HOME
[Environment]::SetEnvironmentVariable("MAVEN_HOME", "C:\apache-maven\apache-maven-3.9.5", "Machine")

# Add to PATH
$path = [Environment]::GetEnvironmentVariable("PATH", "Machine")
$newPath = $path + ";C:\apache-maven\apache-maven-3.9.5\bin"
[Environment]::SetEnvironmentVariable("PATH", $newPath, "Machine")
```

#### Method B: Using Windows GUI
1. Press `Win + R`, type `sysdm.cpl`, press Enter
2. Click "Environment Variables"
3. Under "System Variables":
   - Click "New" → Variable: `MAVEN_HOME`, Value: `C:\apache-maven\apache-maven-3.9.5`
   - Select "Path" → Click "Edit" → Click "New" → Add: `C:\apache-maven\apache-maven-3.9.5\bin`
4. Click OK on all dialogs

### Step 5: Restart PowerShell and Test
```powershell
# Close all PowerShell windows and open a new one
mvn -version
```

---

## 🔧 Alternative: Use Existing Java IDE

If you have IntelliJ IDEA or Eclipse installed, they usually come with Maven bundled:

### IntelliJ IDEA
- Go to File → Settings → Build Tools → Maven
- Use bundled Maven or specify external Maven home

### Eclipse
- Maven is built-in (m2e plugin)
- Go to Window → Preferences → Maven

---

## 🚀 Quick Alternative: Use Maven Wrapper

You can also use the Maven Wrapper that comes with the project:

### For Windows:
```powershell
# Instead of 'mvn', use './mvnw' (if available in project)
.\mvnw clean install
.\mvnw spring-boot:run
```

---

## ✅ Verification Steps

After installation:

1. **Test Maven**:
   ```powershell
   mvn -version
   ```

2. **Expected Output**:
   ```
   Apache Maven 3.9.5
   Maven home: C:\apache-maven\apache-maven-3.9.5
   Java version: 11.0.x
   ```

3. **Test with Project**:
   ```powershell
   cd E:\NourishCare\microservices\config-server
   mvn clean compile
   ```

---

## 🐛 Troubleshooting

### Issue: "mvn is not recognized"
- **Solution**: Environment variables not set correctly
- **Fix**: Restart PowerShell after setting environment variables

### Issue: Java not found
- **Solution**: Install Java 11+ first
- **Download**: https://adoptium.net/

### Issue: Permission denied
- **Solution**: Run PowerShell as Administrator when setting environment variables

---

## 📋 Complete Setup Checklist

- [ ] Java 11+ installed (`java -version`)
- [ ] Maven downloaded and extracted
- [ ] MAVEN_HOME environment variable set
- [ ] Maven bin added to PATH
- [ ] PowerShell restarted
- [ ] Maven working (`mvn -version`)
- [ ] Ready to build NourishCare services!

Once Maven is installed, you can return to the main setup guide and build the microservices.
