package io.rong.imkit.conversation;

import android.view.View;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import io.rong.imkit.conversation.extension.RongExtension;

public abstract class ConversationFragment extends Fragment  {

    // 开启合并转发的选择会话界面
    public static final int REQUEST_CODE_FORWARD = 104;

    /**
     * 获取顶部通知栏容器
     *
     * @return 通知栏容器
     */
    public LinearLayout getNotificationContainer() {
        return null;
    }

    /**
     * 隐藏调用showNotificationView所显示的通知view
     *
     * @param notificationView 通知栏 view
     */
    public void hideNotificationView(View notificationView) {

    }

    /**
     * 在通知区域显示一个view
     */
    public void showNotificationView(View notificationView) {

    }

    public boolean onBackPressed() {
        return false;
    }

    public RongExtension getRongExtension() {
        return null;
    }
}