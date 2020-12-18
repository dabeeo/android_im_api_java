# IMSDK for Android

본 저장소는 IMSDK를 보다 쉽게 적용하기 위한 튜토리얼 프로젝트를 제공합니다.

## API 문서

## 프로젝트 설정

### IMSDK 추가
- ``` IMSDK.aar ``` 파일을 프로젝트 내 ``` app/libs/ ``` 안에 넣어줍니다.

### AndroidManifest
- 필수 권한을 추가합니다

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
    tools:ignore="ScopedStorage" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
```

### build.gradle (project)
- 라이브러리를 탐색하기 위하여 아래의 항목을 추가합니다.

```gradle
allprojects {
  repositories {
      google()
      jcenter()

      flatDir {
          dirs 'libs'
      }
  }
}
```


### build.gradle (app)

- Android SDK 의 최소버전을 설정합니다.

```gradle
  minSdkVersion 24
```
- IMSDK 에서 사용된 필수 Dependency 항목을 추가합니다.

```gradle
dependencies {
  // Kotlin
  implementation "org.jetbrains.kotlin:kotlin-stdlib:$kotlin_version"

  // OkHttp3
  implementation 'com.squareup.okhttp3:okhttp:4.0.1'
  implementation 'com.squareup.okhttp3:logging-interceptor:4.0.1'
  implementation 'com.squareup.okhttp3:okhttp-urlconnection:4.0.1'
  // Retrofit2
  implementation 'com.squareup.retrofit2:retrofit:2.6.1'
  implementation 'com.squareup.retrofit2:converter-gson:2.6.1'
  implementation 'com.squareup.retrofit2:adapter-rxjava2:2.6.1'
  
  // Gson
  implementation 'com.google.code.gson:gson:2.8.5'
  
  // RxJava2
  implementation 'io.reactivex.rxjava2:rxandroid:2.1.1'
  implementation 'io.reactivex.rxjava2:rxjava:2.2.11'
  implementation 'io.reactivex.rxjava2:rxkotlin:2.4.0'

  // IMSDK
  implementation 'com.dabeeo.imsdk:imsdk:1.00.00@aar'
}
```
- 자바 8 이상을 사용하도록 설정합니다.

```gradle
compileOptions {
  sourceCompatibility JavaVersion.VERSION_1_8
  targetCompatibility JavaVersion.VERSION_1_8
}
```
