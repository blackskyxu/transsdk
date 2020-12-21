# TransSDK API 说明文档

##### 一、SDK集成

1 确认 android studio 的 Project 根目录的主 gradle 中配置了 jcenter 支持

```java
allprojects {
    repositories {
        jcenter() // 或者 mavenCentral()
    }
}
```

2 然后在module的build.gradle文件添加依赖项

```java
implementation 'com.transsnet.transsdk:transsdk:1.0.7'
```

3 权限

```java
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

4 混淆配置

```java
#保护注解
-keepattributes *Annotation*
-keep class com.transsnet.transsdk.service** {*;}
-keep class com.transsnet.transsdk.listener.VideoListener {*;}
-keep class com.transsnet.transsdk.manager** {*;}
```

至此，SDK已经顺利接入。

##### 二、接口介绍

主要功能获取静态信息，推荐列表下发及数据回收。

1 初始化类TransConfig。

```java
/**
 * sdk初始化
 * @param context context
 * @param channelKey 渠道key
 */
void init(Context context, String channelKey);
```

使用示例：

```java
public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        TransConfigManager.getTransConfig().init(this, "填入你的channelKey");
        // sdk默认关闭日志
        TransConfigManager.getTransConfig().setLoginEnabled(false);
    }

}
```

```java
/**
 * 是否显示SDK日志
 * @param enabled true 是 false 否
 */
void setLoginEnabled(boolean enabled);
```

```java
/**
 * 销毁资源
 */
void destroy();
```

2 推荐视频管理类VideoService。

```java
/**
 * 添加获取视频监听
 * @param listener 视频数据监听
 */
void addVideoListener(VideoListener listener);

/**
 * 移除之前设置的视频监听
 * @param listener 需要移除的监听器
 */
void removeVideoListener(VideoListener listener);

```

```java
/**
 * 首次加载数据
 */
void loadData();
```

```java
/**
 * 加载更多
 */
void onfresh();
```

推荐视频数据结果监听类VideoListener。

```java
/**
 * 加载更多
 */
void loadMore();
```

```java
/**
 * 刷新回调
 * @param recommendVideoList 推荐数据
 */
void onLoadDataSuccess(List<VideoInfo> recommendVideoList);
```

```java
/**
 * 错误通知
 * @param code 错误码
 * @param msg 错误提示
 */
void onLoadDataFailed(int code, String msg);
```

VideoService使用示例：

```java
public class MainActivity extends AppCompatActivity {

    private boolean isRefresh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registerListener(true);
        loadData();
    }

    private void loadData() {
        // 第一次加载数据
        VideoManager.getVideoService().loadData();
    }

    private void refresh() {
        // 刷新数据
        isRefresh = true;
        VideoManager.getVideoService().refresh();
    }

    private void loadMore() {
        // 加载更多
        VideoManager.getVideoService().loadMore();
    }

    private void registerListener(boolean register) {
        if (register) {
            VideoManager.getVideoService().addVideoListener(mVideoListener);
        } else {
            VideoManager.getVideoService().removeVideoListener(mVideoListener);
        }
    }


    /**
     * 视频数据监听器
     */
    private final VideoListener mVideoListener = new VideoListener() {
        @Override
        public void onLoadDataSuccess(List<VideoInfo> recommendVideoList) {
            // todo 获取数据成功
            if (isRefresh) {
               isRefresh = false;
               // todo
            } else {
               // todo
            }
        }

        @Override
        public void onLoadDataFailed(int code, String msg) {
            // todo 获取数据失败
            isRefresh = false;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        registerListener(false);
    }
}
```

VideoInfo参数说明：

| 参数            | 说明                   |
| --------------- | ---------------------- |
| videoId         | 视频ID                 |
| videoUrl        | 视频播放地址           |
| videoImage      | 视频封面地址           |
| videoWaterUrl   | 带水印视频播放地址     |
| videoWaterImage | 带水印视频封面播放地址 |
| views           | 观看数                 |
| likes           | 点赞数                 |
| shares          | 分项数                 |
| comments        | 评论数                 |
| tag             | 视频分类               |
| authorName      | 作者名称               |
| title           | 视频标题               |
| duration        | 视频播放时长           |
| createdTime     | 视频创建时间           |
| avatarUrl       | 作者头像地址           |

3 数据统计类TransEventService。

```java
/**
 * 页面浏览事件
 * 触发条件：页面切换
 * @param pageID   0 其他页  1 列表页  10 播放页
 */
void pvEvent(PageIDEnum pageID);
```

PageIDEnum 参数说明：

| 参数       | 说明            |
| ---------- | --------------- |
| OTHER_PAGE | 0 其他页面      |
| VIDEO_lIST | 1 视频列表页    |
| VIDEO_PLAY | 10   视频播放页 |

```java
/**
 * 封面曝光事件
 * 触发条件：
 * 视频封面图片高度的 2/3 以上展示在用户可见范围内；
 * 列表快速滑动时不计曝光，停止滑动后才算曝光；
 * 同一视频封面，在单次的页面访问中，重复出现计多次曝光；
 * @param videoId 视频ID
 * @param exposureStamp  曝光时间戳（毫秒）
 * 每次曝光均需生成唯一戳；建议使用 SDK 下发 ID+时间戳生成 16 位 MD5；
 */
void coverExposureEvent(String videoId, String exposureStamp);
```

```java
/**
 * 上报多个封面曝光事件
 * @param exposureEvents
 */
void coverExposureEvents(List<CoverExposureEvent> exposureEvents);
```

CoverExposureEvent参数说明：

| 参数     | 说明       |
| -------- | ---------- |
| videoId  | 视频id     |
| expStamp | 曝光时间戳 |

```java
/**
 *  封面点击事件
 *  触发条件：点击列表页视频封面；
 * @param videoId  视频ID
 * @param exposureStamp  曝光时间戳
 */
void coverClickEvent(String videoId, String exposureStamp);
```

```java
/**
 * 起播事件
 * 触发条件：播放页视频首帧加载完成；
 * @param videoId  视频ID
 * @param exposureStamp 曝光时间戳（毫秒）
 */
void startPlayEvent(String videoId, String exposureStamp);
```

```java
/**
 * 播放事件
 * 触发条件：播放页视频首帧加载完成且结束播放（离开页面或向上下划走）；
 * @param videoId 视频ID
 * @param exposureStamp 曝光时间戳（毫秒））
 * @param duration  播放时长(秒)
 */
void playEvent(String videoId, String exposureStamp, long duration);
```

```java
/**
 * 点赞事件
 * 触发条件：播放页双击屏幕或点击点赞按钮；
 * @param videoId 视频ID
 * @param exposureStamp  曝光时间戳（毫秒）
 * @param likeStatus 点赞状态
 */
void likeEvent(String videoId, String exposureStamp, LikeStatusEnum likeStatus);
```

LikeStatusEnum 参数说明：

| 参数        | 说明       |
| ----------- | ---------- |
| LIKE        | 1 点赞     |
| CANCEL_LIKE | -1取消点赞 |

```java
/**
 * 评论事件
 * 触发条件：播放页成功发布或成功删除评论；
 * @param videoId 视频ID
 * @param exposureStamp 曝光时间戳（毫秒）
 * @param commentStatus 评论状态
 */
void commentEvent(String videoId, String exposureStamp, CommentStatusEnum commentStatus);

```

CommentStatusEnum参数说明：

| 参数           | 说明        |
| -------------- | ----------- |
| COMMENT        | 1 评论      |
| DELETE_COMMENT | -1 取消评论 |

使用示例：

```java
// 上报PV事件
TransEventAgentManager.getTransEventAgent().pvEvent(pageID);		
// 上报封面曝光事件
TransEventAgentManager.getTransEventAgent().coverExposureEvent(videoId, exposureStamp);
// 上报封面曝光事件列表
TransEventAgentManager.getTransEventAgent().coverExposureEvents(events);	
// 上报封面点击事件
TransEventAgentManager.getTransEventAgent().coverClickEvent(videoId, exposureStamp);
//  上报起播事件
TransEventAgentManager.getTransEventAgent().startPlayEvent(videoId, exposureStamp);	
// 上报播放事件
TransEventAgentManager.getTransEventAgent().playEvent(videoId, exposureStamp, duration);
// 上报点赞事件
TransEventAgentManager.getTransEventAgent().likeEvent(videoId, exposureStamp, likeStatus);	
// 上报评论事件
TransEventAgentManager.getTransEventAgent().commentEvent(videoId, exposureStamp, commentStatus);	
```

