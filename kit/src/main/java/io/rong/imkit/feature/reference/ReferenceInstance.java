package io.rong.imkit.feature.reference;

import androidx.fragment.app.Fragment;

import java.lang.ref.WeakReference;

import io.rong.imkit.conversation.extension.RongExtension;

public class ReferenceInstance {
    WeakReference<Fragment> mFragment;
    WeakReference<RongExtension> mRongExtension;
}
