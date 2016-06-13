                                     真机调试错误记录
一：配置<br />
studio推荐2.0及以上<br />
配置SDK 环境：<br />
1.ANDROID_HOME：Android SDK Manager的位置 例如：（PATH => D:\Program Files\Android SDK Tools）<br />
2.设置环境变量PATH：例如：（PATH => %ANDROID_HOME%\tools;%ANDROID_HOME%\platform-tools）<br />

二：apk编译问题<br />
api 等级为23或以上<br />

三：apk安装到设备问题<br />
Could not install the app on the device, read the error above for details.<br />
Make sure you have an Android emulator running or a device connected and have<br />
set up your Android development environment:<br />
https://facebook.github.io/react-native/docs/android-setup.html<br />
解决方法：<br />
1：打开cmd  使用 adb shell 确保能看到设备<br />
2：build.gradle的版本最好为1.2.3<br />


四：红屏问题<br />
1：保证在同一局域网内，不能使用笔记本创建热点，然后手机链接热点<br />


五：测试服务是否开启<br />
http://localhost:8081/index.android.bundle?platform=android<br />
http://192.168.18.115:8081/index.android.bundle?platform=android&dev=true&hot=f
alse&minify=false<br />
测试过程中手机要一直通过usb连接电脑<br />
六:module 0 is not a registered callable module<br />
发生问题原因：<br />
1.使用 react-native init AwesomeProject 创建项目之后 <br />
package.json：<br /> "dependencies": {"react": "^15.1.0",
                                     "react-native": "0.27.2"
                                 }<br />
安卓app目录下的build.gradle：<br />dependencies {
                                  compile fileTree(include: ['*.jar'], dir: 'libs')
                                  compile 'com.android.support:appcompat-v7:23.0.1'
                                 // From node_modules
                                 compile "com.facebook.react:react-native:+"
                                }<br />
2.把 compile "com.facebook.react:react-native:+"改为compile 'com.facebook.react:react-native:0.20.1'<br />
现象：一直报module 0 is not a registered callable module<br />
解决：改回 compile "com.facebook.react:react-native:+"<br />

七：could not connect to developer server（无法连接到开发服务器）<br />
解决：启动服务 react-native start<br />







