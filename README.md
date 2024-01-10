# Weather
날씨 앱
  - Location
  - Foreground Service
  - Android Widget
  - 공공 데이터 포털

## [Location](https://developer.android.com/training/location/permissions?hl=ko)
### 대략적인 위치 vs 정확한 위치
  - 대략적인 위치
      - **ACCESS_COARSE_LOCATION** 권한을 통해 받을 수 있음
      - 런타임 권한
      - 오차범위는 3km  이내
      - 정확한 위치가 필요없는 경우는 대략적인 위치 권한만 사용(날씨 앱 등..)
  - 정확한 위치
      - **ACCESS_FINE_LOCATION** 권한을 통해 받을 수 있음
      - 런타임 권한
      - 오차범위는 50m 이내
      - Android 12 (API 31) 이후부터는 사용자가 대략적인 위치 권한만 허용할 수 있으므로, targetSDK가 31이상일 경우 두 권한을 동시에 요청

### Foreground 위치 vs Background 위치
  - Foreground 위치: 앱을 사용하는 중 요청하는 위치 권한(Background 위치 권한과는 별개)
  - Background 위치: Android 11 이상, 10 이하로 구분
      - 앱의 기능이 Android 10 (API 29)을 실행하는 기기에서 Background 위치를 요청하면 시스템 권한 대화상자에는 '항상 허용'이라는 옵션이 포함된다. 사용자가 이 옵션을 선택하면 앱의 기능에 Background 위치 정보 Access 권한이 부여된다
      - **ACCESS_BACKGROUND_LOCATION** 권한이 필요
      - 매우 위험할 수 있는 기능이므로 Google Play Store에서는 엄격한 위치 정책을 적용하고 있다

### [FusedLocationSource](https://developers.google.com/location-context/fused-location-provider?hl=ko)
  - 간편하고 배터리 효율적인 Android 위치 API
  - 시나리오: CurrentLocation, LastLocation
      - CurrentLocation: 실시간으로 위치가 변하는 경우. ex) 네비게이션
      - LastLocation: 캐싱된 마지막 정보를 불러온다

## [Foreground Service](https://developer.android.com/develop/background-work/services/foreground-services?hl=ko)
  - [서비스 개요](https://developer.android.com/guide/components/services?hl=ko)
  - 앱이 API 26 이상을 대상으로 한다면 앱이 Foreground에 있지 않을 때 시스템에서 [Background Service 실행에 대한 제한](https://developer.android.com/about/versions/oreo/background?hl=ko)을 적용
  - 사용자 개인정보를 보호하기 위해 Android 11 (API 30)에서는 Foreground 서비스가 기기의 위치, 카메라 또는 마이크에 Access 할 수 있는 경우에 대한 제한을 도입
  - 앱이 실행되는 동안 Foreground 서비스를 시작하면 Foreground 서비스에 다음과 같은 제한이 있다
      - ACCESS_BACKGROUND_LOCATION: 사용자가 앱에 권한을 부여하지 않으면 Foreground 서비스가 위치에 Access 할 수 없다
      - Foreground 서비스는 마이크나 카메라에 Access 할 수 없다
   
## [Android Widget](https://developer.android.com/guide/topics/appwidgets?hl=ko)

## [공공 데이터 포털](https://www.data.go.kr/)
  - 정부에서 제공하고 있는 데이터를 활용 신청하여 사용할 수 있음
  - 해당 프로젝트에서는 단기 예보 조회 API를 활용
