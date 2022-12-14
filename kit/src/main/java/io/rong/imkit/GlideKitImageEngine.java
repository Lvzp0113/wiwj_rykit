package io.rong.imkit;

import android.content.Context;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;

//gh_change
public class GlideKitImageEngine implements KitImageEngine {
//    private Transformation<Bitmap> transformation = new CenterCrop();

    @Override
    public void loadCircleImage(@NonNull Context context, @NonNull String url, @NonNull ImageView imageView) {

    }

    @Override
    public void loadCircleImage(@NonNull Context context, @NonNull int resId, @NonNull ImageView imageView) {

    }

    /**
     * 加载图片
     *
     * @param context
     * @param url
     * @param imageView
     */
    @Override
    public void loadImage(@NonNull Context context, @NonNull String url, @NonNull ImageView imageView) {
//        Glide.with(context).load(url).error(R.drawable.rc_received_thumb_image_broken).into(imageView);
    }

    /**
     * 加载相册目录
     *
     * @param context   上下文
     * @param url       图片路径
     * @param imageView 承载图片ImageView
     */
    @Override
    public void loadFolderImage(@NonNull final Context context, @NonNull String url, @NonNull final ImageView imageView) {
//        Glide.with(context)
//                .asBitmap()
//                .override(180, 180)
//                .centerCrop()
//                .sizeMultiplier(0.5f)
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
////                .placeholder(R.drawable.picture_icon_placeholder)
//                .load(url)
//                .into(new BitmapImageViewTarget(imageView) {
//                    @Override
//                    protected void setResource(Bitmap resource) {
//                        RoundedBitmapDrawable circularBitmapDrawable =
//                                RoundedBitmapDrawableFactory.
//                                        create(context.getResources(), resource);
//                        circularBitmapDrawable.setCornerRadius(8);
//                        imageView.setImageDrawable(circularBitmapDrawable);
//                    }
//                });
    }


    /**
     * 加载gif
     *
     * @param context   上下文
     * @param url       图片路径
     * @param imageView 承载图片ImageView
     */
    @Override
    public void loadAsGifImage(@NonNull Context context, @NonNull String url,
                               @NonNull ImageView imageView) {
//        Glide.with(context)
//                .asGif()
//                .load(url)
//                .into(imageView);
    }

    /**
     * 加载图片列表图片
     *
     * @param context   上下文
     * @param url       图片路径
     * @param imageView 承载图片ImageView
     */
    @Override
    public void loadGridImage(@NonNull Context context, @NonNull String url, @NonNull ImageView imageView) {
//        Glide.with(context)
//                .load(url)
//                .override(200, 200)
//                .centerCrop()
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
////                .placeholder(R.drawable.picture_image_placeholder)
//                .into(imageView);
    }


    @Override
    public void loadConversationListPortrait(@NonNull Context context, @NonNull String url, @NonNull ImageView imageView, Conversation conversation) {
//        public void loadConversationListPortrait(@NonNull Context context, @NonNull String url, @NonNull ImageView imageView) {
//        @DrawableRes int resourceId = R.drawable.rc_default_portrait;
//        switch (conversation.getConversationType()) {
//            case GROUP:
//                resourceId = R.drawable.rc_default_group_portrait;
//                break;
//            case CUSTOMER_SERVICE:
//                resourceId = R.drawable.rc_cs_default_portrait;
//                break;
//            case CHATROOM:
//                resourceId = R.drawable.rc_default_chatroom_portrait;
//                break;
//            default:
//                break;
//        }
//        Glide.with(imageView).load(url)
//                .placeholder(resourceId)
//                .error(resourceId)
//                .apply(RequestOptions.bitmapTransform(getPortraitTransformation()))
//                .into(imageView);
    }

    @Override
    public void loadConversationPortrait(@NonNull Context context, @NonNull ImageView avatar, Message message) {
//        public void loadConversationPortrait(@NonNull Context context, @NonNull UiMessage uiMessage, @NonNull ImageView imageView, boolean isSender) {
//        @DrawableRes int resourceId = R.drawable.rc_default_portrait;
//        switch (message.getConversationType()) {
//            case CUSTOMER_SERVICE:
//                if (Message.MessageDirection.RECEIVE == message.getMessageDirection()) {
//                    resourceId = R.drawable.rc_cs_default_portrait;
//                }
//                break;
//            default:
//                break;
//        }
//        Glide.with(imageView).load(url)
//                .placeholder(resourceId)
//                .error(resourceId)
//                .apply(RequestOptions.bitmapTransform(getPortraitTransformation()))
//                .into(imageView);
    }

//    protected Transformation<Bitmap> getPortraitTransformation() {
//        return transformation;
//    }
}
