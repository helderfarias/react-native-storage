
# react-native-storage

## Getting started

`$ npm install react-native-storage --save`

### Mostly automatic installation

`$ react-native link react-native-storage`

### Manual installation

#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-storage` and add `RNStorage.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libRNStorage.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import br.com.helderfarias.storage.RNStoragePackage;` to the imports at the top of the file
  - Add `new RNStoragePackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-storage'
  	project(':react-native-storage').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-storage/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-storage')
  	```


### Additional Libraries iOS (Required)

1. Configure pod
```bash
  cd ios && pod init
```
2. Add FMDB library on file ios/Podfile
```bash
  ...
  pod 'FMDB'
  ...
```
3. Install
```bash
  pod install
```


### Migrations

#### iOS

1. Create directory "assets/migrations" in $PROJECT_DIR/ios/assets/migrations
2. Copy migrations sql in format "1.sql"
```bash
  ...
      ios/
        assets/
          migrations/
            1.sql
            2.sql
            3.sql
  ...
```
3. Open xcode, open $PROJECT_DIR/ios/<project_name>.xcodeproj
4. Add directory "assets"


#### Android

1. Create directory "assets/migrations" in $PROJECT_DIR/android/app/main/assets/migrations
2. Copy migrations sql in format "1.sql"
```bash
  ...
      android/
        app/
          main/
            assets/
              migrations/
                1.sql
                2.sql
                3.sql
  ...
```

## Usage
```javascript
import Storage from 'react-native-storage';

...
  async componentWillMount() {
    let db = await Storage.open("exampledb", 1);

    let rows = await db.query("SELECT * FROM examples");
    console.log(rows);

    let rows = await db.query("SELECT * FROM versions");
    console.log(rows);

    console.log(`Started: ${new Date()}`);
    await db.transaction(async (tx) => {
      for (i = 0; i <= 1000; i++) {
        await tx.insert("versions", { "value": i });
      }      
      await tx.commit();
    });
    console.log(`Ended: ${new Date()}`);    
  }
```
  