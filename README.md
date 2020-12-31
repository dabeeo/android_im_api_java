# IMSDK for Android

본 저장소는 IMSDK를 보다 쉽게 적용하기 위한 튜토리얼 프로젝트를 제공합니다.

## API 문서
- [API 문서로 이동](https://docs.google.com/document/d/1xoOEj1Cjr3eBWwXsoTtHBMRSzQoz5-dYL15Je1l7VMY/edit?usp=sharing)

## 프로젝트 설정

### IMSDK 추가
- ``` imsdk.aar ``` 파일을 프로젝트 내 ``` app/libs/ ``` 안에 넣어줍니다.

### MAPDATA FILE 추가
- ``` mapdata.json ``` 파일을 프로젝트 내 ``` app/src/main/assets/ ``` 안에 넣어줍니다.

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

## 샘플 코드 (Java)
<details>
<summary>지도 불러오기</summary>

### 앱 내에 지도뷰 설정 및 지도데이터가 담긴 JSON 파일로 지도를 불러오는 방법을 설명합니다.

> Layout에 MapView 를 추가합니다.

```xml
<com.dabeeo.imsdk.map.MapView
    android:id="@+id/mapView"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:frameRate="60.0"
    app:renderMode="RENDER_WHEN_DIRTY" />
```
> Callback 을 생성합니다.

```java
private final MapCallback mapCallback = new MapCallback() {
    @Override
    public void onSuccess(List<FloorInfo> list) {
        // 지도 불러오기에 성공하였을 때 호출됩니다.
        // 지도에 포함된 층 데이터가 전달됩니다.
    }

    @Override
    public void onError(Exception e) {
        // 지도 불러오기에 실패하였을 때 호출됩니다.
    }

    @Override
    public void changeFloor(int floor) {
        // 지도의 층이 변경되었을 때 호출됩니다.
        // 변경된 층의 floorLevel 값이 전달됩니다.
    }

    @Override
    public void onClick(double x, double y, Poi poi) {
        // 지도내에서 Poi 클릭 시 호출됩니다.
        // 좌표와 Poi 데이터가 전달됩니다.
    }

    @Override
    public void onLongClick(double x, double y, Poi poi) {
        // 지도내에서 롱클릭 시 호출됩니다.
        // 좌표와 Poi 데이터가 전달됩니다.
    }
};
```

> JSON 파일로 지도를 로드합니다.

```java
mapView.syncMapDataByJson("mapdata.json", mapCallback);
```

</details>

<details>
<summary>지도 제어</summary>

### 지도 내 기능을 제어하는 방법을 설명합니다.

> 지도 확대/축소

```java
// 지도를 한 단계 확대합니다.
mapView.zoomIn();
// 지도를 한 단계 축소합니다.
mapView.zoomOut();
```

> 지도 내 Poi 데이터 가져오기

```java
// 지도 내 모든 Poi 객체를 가져옵니다.
mapView.getPois();
// 지도 내 모든 이동수단 타입을 지닌 Poi 객체를 가져옵니다.
mapView.getTransportationPos();
```

> 지도 이동/회전

```java
// 지정된 좌표로 지도중심점을 이동시킵니다.
mapView.moveToPosition(x, y);
// 지정된 각도만큼 지도를 회전시킵니다.
mapView.rotateToAngle(angle);
```

> 층 변경하기

```java
// 지도의 층을 변경합니다.
// floorLevel 값은 FloorInfo, Poi 데이터에 포함되어 있습니다. 
mapView.setFloor(floorLevel);
```

> 지도 모드 변경

```java
// 지도를 2D/3D 모드로 전환합니다.
// true : 2D 모드
// false : 3D 모드
mapView.changeCamera(true);
```

</details>

<details>
<summary>마커 표시하기</summary>

### 지도 내에 다양한 마커를 추가/삭제하는 방법을 설명합니다.

> 미리 정의된 마커

```java
// 지도상의 좌표 (x, y) 와 층을 지정하여 미리 정의된 마커를 표시합니다.

// 시작위치를 나타내는 마커
mapView.drawStartMarker(x, y, floorLevel);
// 도착위치를 나타내는 마커
mapView.drawEndMarker(x, y, floorLevel);
// 현재위치를 나타내는 마커
mapView.drawNavigationMarker(x, y, floorLevel);
```

> 사용자 정의 마커

```java
// 리소스를 이용하여 지도에 사용자 정의 마커를 추가합니다.
mapView.addMarker(R.drawable.marker, x, y, floorLevel);
mapView.drawMarker();
```

> 마커 제거

```java
// 지도상에 있는 모든 마커를 제거합니다.
mapView.removeMarker();
```
</details>

<details>
<summary>길찾기 요청</summary>

### 출/도착지 및 경유지를 설정하여 길찾기를 요청합니다.

> Callback 을 생성합니다.

```java
private final NavigationListener navigationListener = new NavigationListener() {
    @Override
    public void onPathResult(PathResult pathResult) {
        // 길찾기 요청이 완료되었을 때 호출됩니다.
        // 탐색된 Path 데이터가 전달됩니다.
    }

    @Override
    public void onStart() {
        // 내비게이션이 시작될 때 호출됩니다.
    }

    @Override
    public void onFinish() {
        // 내비게이션이 완료되었을 때 호출됩니다.
    }

    @Override
    public void onCancel() {
        // 내비게이션을 취소하였을 때 호출됩니다.
    }

    @Override
    public void onUpdate(Route route,  Path path,  NodeData nodeData,  Vector3 vector3) {
        // 내비게이션이 진행되고 있을 때 매번 호출됩니다.
        // 현재 진행중인 Path 데이터가 전달됩니다.
    }

    @Override
    public void onRescan() {
        // 내비게이션이 진행 중 재탐색이 되었을 때 호출됩니다.
    }

    @Override
    public void onError(IMError error) {
        // 길찾기 요청을 실패하였을 때 호출됩니다.
        // 에러메시지가 전달됩니다.
    }
};
```

> 시작위치와 도착위치 및 이동수단을 지정하여 길찾기를 요청합니다.

```java
// 출발 위치와 해당 층을 설정합니다.
Vector3 startPosition = new Vector3(x, y, z);
Location startLocation = new Location(startPosition, floorLevel, "시작 위치");

// 도착 위치와 해당 층을 설정합니다.
Vector3 endPosition = new Vector3(x, y, z);
Location endLocation = new Location(endPosition, floorLevel, "도착 위치");

// 경유 위치 목륵을 생성합니다.
List<Location> waypoins = new ArrayList<>();
// 경유지가 존재한다면 추가시키고 없으면 빈 데이터로 설정합니다.

PathRequest pathRequest = new PathRequest(startLocation, endLocation, waypoints, TransType.ALL);
mapView.findPath(pathRequest, navigationListener);
```

</details>

<details>
<summary>내비게이션 기능</summary>
    
### 길찾기 이후 내비게이션 기능을 제어하는 방법을 설명합니다.
    
> 모의보행
    
```java
// 모의보행을 시작합니다.
mapView.startNavigationPreview();
// 모의보행을 종료합니다.
mapView.cancelNavigationPreview();
```

> 내비게이션

```java
// 내비게이션을 시작합니다.
mapView.startNavigation();
// 내비게이션을 종료합니다.
mapView.cancelNavigation();
```
</details>
