# 升级
##更新说明

5.1.2-->5.1.3.4
#### 1.从融云获取最新版kit/sight包
#### 2.覆盖现有kit module
#### 3.覆盖现有sight module 中src/main/java+drawable+layout+清单
#### 3.增量添加现有sight module 中src/main/values
#### 4.移除地图sdk/无用java类
#### 5.与之前逻辑做对比,恢复修改的功能
#### 6.修改版本号

##修改类概况
ConversationConfig              注释代码
InputPanel                      添加构造,暂未添加
InputMode                       新增类型
RongExtensionManager            注释代码
ConversationProcessorFactory    注释代码
BaseMessageItemProvider         glide
MessageItemProviderConfig       修改默认值
?NormalState                    加载消息机制(根据融云给的方式修改)
11111ConversationFragment       移除所有功能,交由子类实现[子类]
BaseConversationProvider        注释代码
ImageEngine                     glide
RouteUtils                      注释代码
GlideKitImageEngine             glide
RongIM                          注释代码
SightExtensionModule            注释代码
SightPlayerActivity             glide
ViewHolder                      glide

##修改注释
//gh_change



> 修改注释

```
    //gh_change
```