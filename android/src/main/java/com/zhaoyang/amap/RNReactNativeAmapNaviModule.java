
package com.zhaoyang.amap;

import android.view.View;

import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Poi;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AmapNaviPage;
import com.amap.api.navi.AmapNaviParams;
import com.amap.api.navi.AmapNaviType;
import com.amap.api.navi.INaviInfoCallback;
import com.amap.api.navi.model.AMapNaviLocation;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class RNReactNativeAmapNaviModule extends ReactContextBaseJavaModule {

  private final ReactApplicationContext reactContext;

  public RNReactNativeAmapNaviModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @Override
  public String getName() {
    return "RNReactNativeAmapNavi";
  }

  @ReactMethod()
  public void showRouteActivity(ReadableArray points,int navType, String requestId,final Promise promise){
    if(points==null || points.size() <1) {
      promise.reject("-1","POI数量不能少于1");
      return;
    }
    Poi start = null, end = null;
    List<Poi> wayList = new ArrayList();//途径点目前最多支持3个。
    //默认当前作为作为起点，该点作为结束位置
    for (int i=0;i<points.size();i++) {
      ReadableMap map = points.getMap(i);
      Poi poi = new Poi(map.getString("name"),
              new LatLng(map.getDouble("latitude"),map.getDouble("longitude")),map.hasKey("POIId")?map.getString("POIId"):"");
      if(i == 0)
      {
        if(points.size() == 1) {
          end = poi;
        } else {
          start = poi;
        }
      } else if(i == points.size()-1) {
        end = poi;
      } else {
        wayList.add(poi);
      }
    }
    AmapNaviPage.getInstance().showRouteActivity(this.reactContext, new AmapNaviParams(start, wayList, end, AmapNaviType.values()[navType]),new NaviInfoCallback(this.reactContext, requestId));
    promise.resolve(true);
  }

  @ReactMethod()
  public void stopNavi(){
    AMapNavi aMapNavi = AMapNavi.getInstance(getCurrentActivity().getApplicationContext());
    aMapNavi.stopNavi();
  }

  @ReactMethod()
  public void destoryNavi(){
    AMapNavi aMapNavi = AMapNavi.getInstance(getCurrentActivity().getApplicationContext());
    aMapNavi.destroy();
  }
}

class NaviInfoCallback implements INaviInfoCallback {
  private final ReactApplicationContext reactContext;

  public NaviInfoCallback(ReactApplicationContext reactContext, String requestId) {
    this.reactContext = reactContext;
    this.requestId = requestId;
  }

  private void sendEvent(ReactContext reactContext,
                         String eventName,
                         @Nullable WritableMap params) {
    reactContext
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
            .emit(eventName, params);
  }

  @Override
  public void onInitNaviFailure() {

  }

  @Override
  public void onGetNavigationText(String s) {

  }

  @Override
  public void onLocationChange(AMapNaviLocation aMapNaviLocation) {
    WritableMap map = Arguments.createMap();
    map.putBoolean("requestId",this.requestId);
    sendEvent(this.reactContext,"onLocationChange",map);
  }

  @Override
  public void onArriveDestination(boolean b) {
    WritableMap map = Arguments.createMap();
    map.putBoolean("isArrive",b);
    map.putBoolean("requestId",this.requestId);
    sendEvent(this.reactContext,"onArriveDestination",map);
  }

  @Override
  public void onStartNavi(int i) {
    WritableMap map = Arguments.createMap();
    map.putBoolean("requestId",this.requestId);
    sendEvent(this.reactContext,"onStartNavi",map);
  }

  @Override
  public void onCalculateRouteSuccess(int[] ints) {
    WritableMap map = Arguments.createMap();
    map.putBoolean("requestId",this.requestId);
    sendEvent(this.reactContext,"onCalculateRouteSuccess",map);
  }

  @Override
  public void onCalculateRouteFailure(int i) {
    WritableMap map = Arguments.createMap();
    map.putBoolean("requestId",this.requestId);
    sendEvent(this.reactContext,"onCalculateRouteFailure",map);
  }

  @Override
  public void onStopSpeaking() {

  }

  @Override
  public void onReCalculateRoute(int i) {
    WritableMap map = Arguments.createMap();
    map.putBoolean("requestId",this.requestId);
    sendEvent(this.reactContext,"onReCalculateRoute",map);
  }

  @Override
  public void onExitPage(int i) {
    WritableMap map = Arguments.createMap();
    map.putBoolean("requestId",this.requestId);
    sendEvent(this.reactContext,"onExitPage",map);
  }

  @Override
  public void onStrategyChanged(int i) {
    WritableMap map = Arguments.createMap();
    map.putInt("type",i);
    map.putBoolean("requestId",this.requestId);
    sendEvent(this.reactContext,"onStrategyChanged",map);
  }

  @Override
  public View getCustomNaviBottomView() {
    return null;
  }

  @Override
  public View getCustomNaviView() {
    return null;
  }

  @Override
  public void onArrivedWayPoint(int i) {
    WritableMap map = Arguments.createMap();
    map.putInt("index",i);
    map.putBoolean("requestId",this.requestId);
    sendEvent(this.reactContext,"onArrivedWayPoint",map);
  }

  @Override
  public void onMapTypeChanged(int i) {
    WritableMap map = Arguments.createMap();
    map.putInt("type",i);
    map.putBoolean("requestId",this.requestId);
    sendEvent(this.reactContext,"onMapTypeChanged",map);
  }

  @Override
  public View getCustomMiddleView() {
    return null;
  }
}
