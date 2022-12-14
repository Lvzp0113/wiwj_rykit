package io.rong.imkit.config;

import android.content.Context;
import android.content.res.Resources;
import android.text.Spannable;
import android.text.SpannableString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import io.rong.common.RLog;
import io.rong.imkit.R;
import io.rong.imkit.conversation.extension.component.moreaction.DeleteClickActions;
import io.rong.imkit.conversation.extension.component.moreaction.IClickActions;
import io.rong.imkit.conversation.messgelist.processor.IConversationUIRenderer;
import io.rong.imkit.conversation.messgelist.provider.DefaultMessageItemProvider;
import io.rong.imkit.conversation.messgelist.provider.IConversationSummaryProvider;
import io.rong.imkit.conversation.messgelist.provider.IMessageProvider;
import io.rong.imkit.conversation.messgelist.viewmodel.IMessageViewModelProcessor;
import io.rong.imkit.feature.forward.ForwardClickActions;
import io.rong.imkit.model.UiMessage;
import io.rong.imkit.widget.adapter.ProviderManager;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.MessageContent;

/**
 * 会话页面总配置项
 */
public class ConversationConfig {

    private static final int conversationHistoryMessageMaxCount = 100;
    //离线补偿已读回执，sp 文件名称
    public static String SP_NAME_READ_RECEIPT_CONFIG = "readReceiptConfig";
    /**
     * 多端消息未读数同步，仅支持单群聊
     */
    public static boolean enableMultiDeviceSync = true;
    private static int conversationRemoteMessageMaxCount = 100;
    private static int conversationShowUnreadMessageMaxCount = 100;
    private final String TAG = "ConversationConfig";
    //消息撤回开关
    public boolean rc_enable_recall_message = true;
    //消息重发开关
    public boolean rc_enable_resend_message = true;
    public int rc_message_recall_interval = 120;
    public int rc_message_recall_edit_interval = 300;
    public int rc_chatroom_first_pull_message_count = 10;
    public boolean rc_is_show_warning_notification = true;
    // 设置未听的语音消息，是否连续播放
    public boolean rc_play_audio_continuous = true;
    // 是否打开 @ 功能
    public boolean rc_enable_mentioned_message = true;
    // 设置已读回执有效时间（单位：秒）
    public int rc_read_receipt_request_interval = 120;
    // 选择媒体资源时，是否包含视频文件
    public boolean rc_media_selector_contain_video = false;
    // 在线时是否自动下载高质量语音消息
    public boolean rc_enable_automatic_download_voice_msg = true;
    // gif 自动下载的最大值， 超过就需点击手动下载（单位 KB）
    public int rc_gifmsg_auto_download_size = 1024;
    // 多选时,最大多选消息数
    public int rc_max_message_selected_count = 100;
    // 是否开启合并转发功能,默认关闭
    public boolean rc_enable_send_combine_message = false;
    private long rc_custom_service_evaluation_interval = 60 * 1000L;
    private boolean mStopCSWhenQuit = true;
    /**
     * 已读回执，仅支持，单，群聊
     */
    private boolean mEnableReadReceipt = true;
    private Set<Conversation.ConversationType> mSupportReadReceiptConversationTypes = new HashSet<>(4);
    /**
     * 单聊是否显示头像
     */
    private boolean showReceiverUserTitle = false;
    /**
     * 新消息是否显示未读，目前支持 单，群聊
     */
    private boolean showNewMessageBar = true;
    /**
     * 历史消息是否显示，目前仅支持，单，群聊
     */
    private boolean showHistoryMessageBar = true;
    /**
     * 长按是否显示更多
     */
    private boolean showMoreClickAction = true;
    /**
     * 是否显示，历史消息模板
     */
    private boolean showHistoryDividerMessage = true;
    private ConversationClickListener mConversationClickListener;
    private OnSendMessageListener mOnSendMessageListener;
    private ProviderManager<UiMessage> mMessageListProvider = new ProviderManager<>();
    private List<IConversationUIRenderer> mConversationViewProcessors = new ArrayList<>();
    private List<IConversationSummaryProvider> mConversationSummaryProviders = new ArrayList<>();
    private CopyOnWriteArrayList<IClickActions> mMoreClickActions = new CopyOnWriteArrayList<>();
    private IMessageProvider defaultMessageProvider = new DefaultMessageItemProvider();
    private IMessageViewModelProcessor mViewModelProcessor;
    // 是否显示未读 @消息
    private boolean showNewMentionMessageBar = true;
    // 进入会话界面，默认拉取历史消息数量
    private int conversationHistoryMessageCount = 10;
    // 进入会话界面，默认拉取远端历史消息数量
    private int conversationRemoteMessageCount = 10;
    // 进入会话界面，默认显示未读消息数量
    private int conversationShowUnreadMessageCount = 10;


    ConversationConfig() {
        initMessageProvider();
        initViewProcessor();
        initMoreClickAction();
        mSupportReadReceiptConversationTypes.add(Conversation.ConversationType.PRIVATE);
        mSupportReadReceiptConversationTypes.add(Conversation.ConversationType.ENCRYPTED);
        mSupportReadReceiptConversationTypes.add(Conversation.ConversationType.GROUP);
        mSupportReadReceiptConversationTypes.add(Conversation.ConversationType.DISCUSSION);
    }

    public void initConfig(Context context) {
        if (context != null) {
            Resources resources = context.getResources();
            try {
                rc_enable_recall_message = resources.getBoolean(R.bool.rc_enable_message_recall);
            } catch (Exception e) {
                RLog.e(TAG, "rc_enable_recall_message not get value", e);
            }
            try {
                rc_message_recall_interval = resources.getInteger(R.integer.rc_message_recall_interval);
            } catch (Exception e) {
                RLog.e(TAG, "rc_message_recall_interval not get value", e);
            }
            try {
                rc_message_recall_edit_interval = resources.getInteger(R.integer.rc_message_recall_edit_interval);
            } catch (Exception e) {
                RLog.e(TAG, "rc_message_recall_edit_interval not get value", e);
            }
            try {
                rc_chatroom_first_pull_message_count = resources.getInteger(R.integer.rc_chatroom_first_pull_message_count);
            } catch (Exception e) {
                RLog.e(TAG, "rc_chatroom_first_pull_message_count not get value", e);
            }
            try {
                mEnableReadReceipt = resources.getBoolean(R.bool.rc_read_receipt);
            } catch (Exception e) {
                RLog.e(TAG, "rc_read_receipt not get value", e);
            }
            try {
                enableMultiDeviceSync = resources.getBoolean(R.bool.rc_enable_sync_read_status);
            } catch (Exception e) {
                RLog.e(TAG, "rc_enable_sync_read_status not get value", e);
            }
            try {
                rc_play_audio_continuous = resources.getBoolean(R.bool.rc_play_audio_continuous);
            } catch (Exception e) {
                RLog.e(TAG, "rc_play_audio_continuous not get value", e);
            }
            try {
                rc_enable_mentioned_message = resources.getBoolean(R.bool.rc_enable_mentioned_message);
            } catch (Exception e) {
                RLog.e(TAG, "rc_enable_mentioned_message not get value", e);
            }
            try {
                rc_read_receipt_request_interval = resources.getInteger(R.integer.rc_read_receipt_request_interval);
            } catch (Exception e) {
                RLog.e(TAG, "rc_enable_mentioned_message not get value", e);
            }
            try {
                rc_media_selector_contain_video = resources.getBoolean(R.bool.rc_media_selector_contain_video);
            } catch (Exception e) {
                RLog.e(TAG, "rc_media_selector_contain_video not get value", e);
            }
            try {
                rc_enable_automatic_download_voice_msg = resources.getBoolean(R.bool.rc_enable_automatic_download_voice_msg);
            } catch (Exception e) {
                RLog.e(TAG, "rc_enable_automatic_download_voice_msg not get value", e);
            }
            try {
                rc_gifmsg_auto_download_size = resources.getInteger(R.integer.rc_gifmsg_auto_download_size);
            } catch (Exception e) {
                RLog.e(TAG, "rc_gifmsg_auto_download_size not get value", e);
            }
            try {
                rc_max_message_selected_count = resources.getInteger(R.integer.rc_max_message_selected_count);
            } catch (Exception e) {
                RLog.e(TAG, "rc_max_message_selected_count not get value", e);
            }
            try {
                rc_enable_send_combine_message = resources.getBoolean(R.bool.rc_enable_send_combine_message);
            } catch (Exception e) {
                RLog.e(TAG, "rc_enable_send_combine_message not get value", e);
            }

            try {
                showNewMentionMessageBar = resources.getBoolean(R.bool.rc_enable_unread_mention);
            } catch (Exception e) {
                RLog.e(TAG, "rc_enable_unread_mention not get value", e);
            }

            try {
                conversationHistoryMessageCount = resources.getInteger(R.integer.rc_conversation_history_message_count);
                if (conversationHistoryMessageCount > conversationHistoryMessageMaxCount) {
                    conversationHistoryMessageCount = conversationHistoryMessageMaxCount;
                }
            } catch (Exception e) {
                RLog.e(TAG, "rc_conversation_history_message_count not get value", e);
            }

            try {
                conversationRemoteMessageCount = resources.getInteger(R.integer.rc_conversation_remote_message_count);
                if (conversationRemoteMessageCount > conversationRemoteMessageMaxCount) {
                    conversationRemoteMessageCount = conversationRemoteMessageMaxCount;
                }
            } catch (Exception e) {
                RLog.e(TAG, "rc_conversation_remote_message_count not get value", e);
            }

            try {
                conversationShowUnreadMessageCount = resources.getInteger(R.integer.rc_conversation_show_unread_message_count);
                if (conversationShowUnreadMessageCount > conversationShowUnreadMessageMaxCount) {
                    conversationShowUnreadMessageCount = conversationShowUnreadMessageMaxCount;
                }
            } catch (Exception e) {
                RLog.e(TAG, "rc_conversation_show_unread_message_count not get value", e);
            }
        }
    }

    private void initMessageProvider() {
        mMessageListProvider.setDefaultProvider(defaultMessageProvider);
        //gh_change
//        addMessageProvider(new TextMessageItemProvider());
//        addMessageProvider(new ImageMessageItemProvider());
//        addMessageProvider(new DestructImageMessageItemProvider());
//        addMessageProvider(new HQVoiceMessageItemProvider());
//        addMessageProvider(new FileMessageItemProvider());
//        addMessageProvider(new GIFMessageItemProvider());
//        addMessageProvider(new InformationNotificationMessageItemProvider());
//        addMessageProvider(new RecallNotificationMessageItemProvider());
//        addMessageProvider(new RichContentMessageItemProvider());
//        addMessageProvider(new ReferenceMessageItemProvider());
//        addMessageProvider(new SightMessageItemProvider());
//        addMessageProvider(new DestructSightMessageItemProvider());
//        addMessageProvider(new DestructGifMessageItemProvider());
//        addMessageProvider(new DestructTextMessageItemProvider());
//        addMessageProvider(new DestructHQVoiceMessageItemProvider());
//        addMessageProvider(new DestructVoiceMessageItemProvider());
//        addMessageProvider(new HistoryDivMessageItemProvider());
//        addMessageProvider(new CombineMessageItemProvider());
//        addMessageProvider(new LocationMessageItemProvider());
//        addMessageProvider(new VoiceMessageItemProvider());
//        addMessageProvider(new GroupNotificationMessageItemProvider());
//        addMessageProvider(new RealTimeLocationMessageItemProvider());
//        addMessageProvider(new CSPullLeaveMsgItemProvider());
//        addMessageProvider(new UnknownMessageItemProvider());
//        addMessageProvider(new PublicServiceMultiRichContentMessageProvider());
//        addMessageProvider(new PublicServiceRichContentMessageProvider());
    }

    private void initViewProcessor() {
        //gh_change
//        mConversationViewProcessors.add(new CSConversationUIRenderer());
//        mConversationViewProcessors.add(new LocationUiRender());
    }

    private void initMoreClickAction() {
        mMoreClickActions.add(new ForwardClickActions());
        mMoreClickActions.add(new DeleteClickActions());
    }

    /**
     * /~chiense
     * @param index  添加位置
     * @param action 添加点击更多事件
     * @return
     */

    /**
     * /~english
     *
     * @param index  Add location
     * @param action Add click more events
     */
    public void addMoreClickAction(int index, IClickActions action) {
        if (action != null)
            mMoreClickActions.add(index, action);
    }

    /**
     * /~chinese
     * ConversationFragment 处理器
     *
     * @param processor
     * @return
     */

    /**
     * /~english
     * ConversationFragment processor
     *
     * @param processor IConversationUIRenderer
     */
    public void addViewProcessor(IConversationUIRenderer processor) {
        mConversationViewProcessors.add(processor);
    }

    /**
     * /~chinese
     * @return ConversationFragment 处理器
     */

    /**
     * /~english
     * @return ConversationFragment processor
     */
    public List<IConversationUIRenderer> getViewProcessors() {
        return mConversationViewProcessors;
    }

    /**
     * /~chinese
     * @param provider 消息列表 item 提供者
     * @return
     */

    /**
     * /~english
     * @param provider  Message list item provider
     */
    public void addMessageProvider(IMessageProvider provider) {
        if (provider != null) {
            mMessageListProvider.addProvider(provider);
            mConversationSummaryProviders.add(provider);
        }
    }

    /**
     * /~chinese
     * 替换已有的模板
     *
     * @param oldProviderClass 旧模板 class 类
     * @param provider         新模板
     */

    /**
     * /~english
     * Replace an existing template
     *
     * @param oldProviderClass Old template class
     * @param provider New template
     */
    public void replaceMessageProvider(Class oldProviderClass, IMessageProvider provider) {
        mMessageListProvider.replaceProvider(oldProviderClass, provider);
        int index = -1;
        for (int i = 0; i < mConversationSummaryProviders.size(); i++) {
            IConversationSummaryProvider item = mConversationSummaryProviders.get(i);
            if (item.getClass().equals(oldProviderClass)) {
                index = i;
                break;
            }
        }
        if (index != -1) {
            mConversationSummaryProviders.set(index, provider);
        }

    }

    /**
     * /~chinese
     * @return 获得消息模板列表
     */

    /**
     * /~english
     * @return Get a list of message templates
     */
    public ProviderManager<UiMessage> getMessageListProvider() {
        return mMessageListProvider;
    }

    /**
     * /~chinese
     * 获得消息展示信息
     *
     * @param context        上下文
     * @param messageContent 消息类型
     * @return
     */

    /**
     * /~english
     * Get message display information
     *
     * @param context Context
     * @param messageContent Message type
     * @return
     */
    public Spannable getMessageSummary(Context context, MessageContent messageContent) {
        Spannable spannable = new SpannableString("");
        Spannable defaultSpannable = defaultMessageProvider.getSummarySpannable(context, messageContent);
        if (messageContent == null) {
            return spannable;
        }
        for (IConversationSummaryProvider item : mConversationSummaryProviders) {
            if (item.isSummaryType(messageContent)) {
                spannable = item.getSummarySpannable(context, messageContent);
            }
        }
        return spannable == null ? defaultSpannable : spannable;
    }

    /**
     * /~chinese
     * 是否在消息列表显示名称
     *
     * @param messageContent 消息类型
     * @return
     */

    /**
     * /~english
     * Whether to display the name in the message list
     *
     * @param messageContent Message type
     * @return
     */
    public boolean showSummaryWithName(MessageContent messageContent) {
        for (IConversationSummaryProvider item : mConversationSummaryProviders) {
            if (item.isSummaryType(messageContent)) {
                return item.showSummaryWithName();
            }
        }
        return false;
    }

    /**
     * /~chinese
     * @param showReceiverUserTitle 单聊是否显示用户昵称
     */

    /**
     * /~english
     * @param showReceiverUserTitle Whether a single chat displays the user's nickname
     */
    public void setShowReceiverUserTitle(boolean showReceiverUserTitle) {
        this.showReceiverUserTitle = showReceiverUserTitle;
    }

    /**
     * /~chinese
     * 单聊是否显示用户昵称
     * 仅支持配置单聊属性
     *
     * @param type 会话类型
     * @return 是否显示
     */

    /**
     * /~english
     * Whether a single chat displays user nicknames. Only the configuration of single chat attributes is supported
     *
     * @param type Conversation type
     * @return Whether to display
     */
    public boolean isShowReceiverUserTitle(Conversation.ConversationType type) {
        if (!showReceiverUserTitle) {
            switch (type) {
                case PRIVATE:
                case ENCRYPTED:
                    return false;
            }
        }
        return true;
    }

    /**
     * /~chinese
     * @return 长按是否显示更多选项
     */

    /**
     * /~english
     * @return Does the hold show more options?
     */
    public boolean isShowMoreClickAction() {
        return showMoreClickAction;
    }

    /**
     * /~chinese
     * @param showMoreClickAction 长按是否显示更多选项
     */

    /**
     * /~english
     * @param showMoreClickAction Hold to check whether to show more options
     */
    public void setShowMoreClickAction(boolean showMoreClickAction) {
        this.showMoreClickAction = showMoreClickAction;
    }

    /**
     * /~chinese
     * @return 是否显示历史消息模板
     */

    /**
     * /~english
     * @return Whether to display the historical message template
     */
    public boolean isShowHistoryDividerMessage() {
        return showHistoryDividerMessage;
    }

    /**
     * /~chinese
     * @param showHistoryDividerMessage 是否显示历史消息模板
     */

    /**
     * /~english
     * @return  Whether to display the historical message template
     */
    public void setShowHistoryDividerMessage(boolean showHistoryDividerMessage) {
        this.showHistoryDividerMessage = showHistoryDividerMessage;
    }

    /**
     * /~chinese
     * @param showNewMessageBar 新消息是否显示未读气泡，目前仅支持单群聊（聊天室等，设置无效）
     */

    /**
     * /~english
     * @return Whether unread bubbles are displayed in new messages. Currently, only single group chat is supported (settings such as chatrooms are invalid)
     */
    public void setShowNewMessageBar(boolean showNewMessageBar) {
        this.showNewMessageBar = showNewMessageBar;
    }

    /**
     * /~chinese
     * 新消息是否显示未读气泡，目前仅支持单群聊
     *
     * @param type 会话类型
     * @return 不支持类型返回false，支持类型返回 showHistoryMessageBar 值
     */

    /**
     * /~english
     * Whether the new message shows unread bubbles. Currently, only single group chat is supported
     *
     * @param type Conversation type
     * @return Return false for non-supported type and return showHistoryMessageBar value for the supported type
     */
    public boolean isShowNewMessageBar(Conversation.ConversationType type) {
        if (showNewMessageBar) {
            switch (type) {
                case PRIVATE:
                case GROUP:
                case DISCUSSION:
                case ENCRYPTED:
                    return true;
            }
        }
        return false;
    }

    /**
     * /~chinese
     * 会话页面右上角的未读 @ 消息数提示，目前仅支持群聊
     *
     * @param type 会话类型
     * @return 不支持类型返回 false，支持类型返回 showNewMentionMessageBar 值
     */

    /**
     * /~english
     * The number of unread @ messages at the upper right corner of the conversation page is prompted. Currently, only group chat is supported.
     *
     * @param type Conversation type
     * @return Do not support type return false, support type return showNewMentionMessageBar value
     */
    public boolean isShowNewMentionMessageBar(Conversation.ConversationType type) {
        if (showNewMentionMessageBar) {
            switch (type) {
                case GROUP:
                case DISCUSSION:
                    return true;
            }
        }
        return false;
    }

    /**
     * /~chinese
     * @param showNewMentionMessageBar 是否显示会话页面右上角的未读 @ 消息数提示，仅支持设置群组
     */

    /**
     * /~english
     * @param showNewMentionMessageBar Whether to display the number of unread @ messages in the upper right corner of the conversation page. You can only set groups
     */
    public void setShowNewMentionMessageBar(boolean showNewMentionMessageBar) {
        this.showNewMentionMessageBar = showNewMentionMessageBar;
    }

    public int getConversationHistoryMessageCount() {
        return conversationHistoryMessageCount;
    }

    public void setConversationHistoryMessageCount(int conversationHistoryMessageCount) {
        this.conversationHistoryMessageCount = conversationHistoryMessageCount;
    }

    public int getConversationRemoteMessageCount() {
        return conversationRemoteMessageCount;
    }

    public void setConversationRemoteMessageCount(int conversationRemoteMessageCount) {
        this.conversationRemoteMessageCount = conversationRemoteMessageCount;
    }

    public int getConversationShowUnreadMessageCount() {
        return conversationShowUnreadMessageCount;
    }

    public void setConversationShowUnreadMessageCount(int conversationShowUnreadMessageCount) {
        this.conversationShowUnreadMessageCount = conversationShowUnreadMessageCount;
    }

    /**
     * /~chinese
     * @param showHistoryMessageBar 是否显示历史未读消息气泡，仅支持设置私聊，群组
     */

    /**
     * /~english
     * @param showHistoryMessageBar  Whether to display the bubble of historical unread messages. Only private chat and group are supported
     */
    public void setShowHistoryMessageBar(boolean showHistoryMessageBar) {
        this.showHistoryMessageBar = showHistoryMessageBar;
    }

    /**
     * /~chinese
     * 是否显示历史未读消息气泡，仅支持设置私聊，群组
     *
     * @param type 会话类型
     * @return 不支持类型返回false，支持类型返回 showHistoryMessageBar 值
     */

    /**
     * /~english
     * Whether to display the bubble of historical unread messages. Only private chat and group are supported
     *
     * @param type Conversation type
     * @return Return false for non-supported type and return showHistoryMessageBar value for the supported type
     */
    public boolean isShowHistoryMessageBar(Conversation.ConversationType type) {
        if (showHistoryMessageBar) {
            switch (type) {
                case PRIVATE:
                case GROUP:
                case DISCUSSION:
                case ENCRYPTED:
                    return true;
            }
        }
        return false;
    }

    public ConversationClickListener getConversationClickListener() {
        return mConversationClickListener;
    }

    public void setConversationClickListener(ConversationClickListener conversationClickListener) {
        mConversationClickListener = conversationClickListener;
    }

    /**
     * /~chinese
     * 获取会话页面长按消息，弹出框里点击"更多"选项时，底部需要显示的条目。
     * 可以通过对此列表的增删，进行自定义显示。
     *
     * @return 当前设置的点击"更多"时底部显示的条目列表。
     */

    /**
     * /~english
     * Get the hold message on the conversation page and he entry that shall be displayed at the bottom when you click the "more" option in the pop-up box. You can customize the display by adding or deleting this list.
     * @return The list of entries displayed at the bottom of the current setting when you click "more".
     */
    public List<IClickActions> getMoreClickActions() {
        return mMoreClickActions;
    }

    public IMessageViewModelProcessor getViewModelProcessor() {
        return mViewModelProcessor;
    }

    public void setViewModelProcessor(IMessageViewModelProcessor viewModelProcessor) {
        mViewModelProcessor = viewModelProcessor;
    }

    /**
     * /~chinese
     * 设置已读回执，仅支持单聊，群聊，讨论组，密聊，其余不生效
     *
     * @param enable 回执开关
     * @return
     */

    /**
     * /~english
     * Set read receipt. Only single chat, group chat, discussion group and secret chat are supported and the rest are not valid
     *
     * @param enable  Return receipt switch
     */
    public void setEnableReadReceipt(boolean enable) {
        mEnableReadReceipt = enable;
    }

    public void setSupportReadReceiptConversationType(Conversation.ConversationType... types) {
        mSupportReadReceiptConversationTypes.clear();
        mSupportReadReceiptConversationTypes.addAll(Arrays.asList(types));
    }

    /**
     * /~chinese
     * 仅适用单聊和加密
     *
     * @param type 会话类型
     * @return 不支持类型返回 false, 支持类型 enableReadReceipt 值
     */

    /**
     * /~english
     * Only for single chat and encryption
     *
     * @param type Conversation type
     * @return Do not support type return false, support type enableReadReceipt value
     */
    public boolean isShowReadReceipt(Conversation.ConversationType type) {
        if (mEnableReadReceipt) {
            switch (type) {
                case PRIVATE:
                case ENCRYPTED:
                    return mSupportReadReceiptConversationTypes.contains(type);
            }
        }
        return false;
    }

    /**
     * /~chinese
     * 仅适用群聊和讨论组
     *
     * @param type 会话类型
     * @return 不支持类型返回 false, 支持类型返回 enableReadReceipt 值
     */

    /**
     * /~english
     * Group chat and discussion groups only
     *
     * @param type Conversation type
     * @return Do not support type return false, support type return enableReadReceipt value
     */
    public boolean isShowReadReceiptRequest(Conversation.ConversationType type) {
        if (mEnableReadReceipt) {
            switch (type) {
                case GROUP:
                case DISCUSSION:
                    return mSupportReadReceiptConversationTypes.contains(type);
            }
        }
        return false;
    }

    /**
     * /~chinese
     * 是否打开多端阅读状态同步功能。 开启之后，在其它端阅读过的消息，当前客户端会同步清掉未读数。
     *
     * @param type 会话类型。该功能仅支持单聊、群聊。
     * @return 功能是否开启。
     */

    /**
     * /~english
     * Whether to enable the multi-terminal reading status synchronization function When enabled, the current client will synchronously clear the unread of the messages that have been read on the other side.
     *
     * @param type Conversation type This function only supports individual chat and group chat.
     * @return Whether the function is enabled or not.
     */
    public boolean isEnableMultiDeviceSync(Conversation.ConversationType type) {
        if (enableMultiDeviceSync) {
            switch (type) {
                case PRIVATE:
                case ENCRYPTED:
                case GROUP:
                case DISCUSSION:
                    return true;
            }
        }
        return false;
    }

    public void setEnableMultiDeviceSync(boolean enableMultiDeviceSync) {
        this.enableMultiDeviceSync = enableMultiDeviceSync;
    }

    public void setSendMessageListener(OnSendMessageListener listener) {
        this.mOnSendMessageListener = listener;
    }

    public OnSendMessageListener getSendMessageLister() {
        return mOnSendMessageListener;
    }
}
