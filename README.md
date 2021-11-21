#### 使用说明文档

双向滑动控件
> 用于社交软件，年龄、身高、体重等范围选择

#### 效果预览

<img src="https://raw.githubusercontent.com/Leo199206/BidirectionalSeekBar/device-2021-11-21-105818.gif" width="300" heght="500" align=center />

#### 依赖

+ 添加maven仓库配置到项目根目录gradle文件下

```
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

+ 添加以下maven依赖配置到app模块，gradle文件下

```
implementation  'com.github.Leo199206:BidirectionalSeekBar:1.0.8'
```

#### 添加到布局

```
    <com.plant.bidirectionalseekbar.BidirectionalSeekBar
        android:id="@+id/seekbar_age"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:padding="20dp"
        android:paddingLeft="15dp"
        android:paddingTop="20dp"
        android:paddingRight="15dp"
        android:paddingBottom="20dp"
        app:thumbDisableColor="@color/white"
        app:thumbEnableColor="@color/white"
        app:thumbSize="23dp"
        app:thumbStrokeDisableColor="@color/teal_700"
        app:thumbStrokeEnableColor="@color/teal_200"
        app:thumbStrokeSize="25dp"
        app:trackBgDisableColor="@color/cardview_shadow_start_color"
        app:trackBgEnableColor="@color/cardview_shadow_start_color"
        app:trackFgDisableColor="@color/teal_700"
        app:trackFgEnableColor="@color/teal_200" />

```

+ 代码配置

```
seekbarAge.init(18f, 80f, 20f, 40f, object :
            BidirectionalSeekBar.OnSeekBarChangeLister {

            override fun onSeekBarChange(
                view: BidirectionalSeekBar,
                startProgress: Float,
                endProgress: Float,
                startValue: Float,
                endValue: Float
            ) {
                binding.tvAge.text = "age: ${startValue.toInt()} - ${endValue.toInt()}"
            }

            override fun onUnEnable(view: BidirectionalSeekBar) {
            }
        })

```

#### 已定义样式属性

| 属性  | 说明 |
| --- | --- |
| thumbEnableColor | 可滑动状态下，滑块颜色 |
| thumbDisableColor | 不可滑动状态下，滑块颜色 |
| thumbStrokeEnableColor | 可滑动状态下，底层滑块颜色 |
| thumbStrokeDisableColor | 不可滑动状态下，底层滑块颜色  |
| trackBgEnableColor | 可滑动状态下，度总长度颜色  | 
| trackBgDisableColor | 不可滑动状态下，进度总长度颜色 | 
| trackFgEnableColor | 可滑动状态下，进度条颜色  |
| trackFgDisableColor | 不可滑动状态下，进度条颜色  | 
| thumbSize | 滑块大小 | 
| thumbStrokeSize | 滑块外圈大小，此处请直接给圆大小 |

#### 支持

+ 使用过程中，如有问题或者建议，欢迎提出issue
+ 如遇issue解决不了的问题，可以邮件联系：850884963@qq.com
+ 开源维护不易，如果该库对你有帮助，请动动你的小手指，给个star🤩

#### LICENSE

SlideUnlock is under the Apache License Version 2.0. See
the [LICENSE](https://raw.githubusercontent.com/Leo199206/SlideUnlock/main/LICENSE) file for
details.
