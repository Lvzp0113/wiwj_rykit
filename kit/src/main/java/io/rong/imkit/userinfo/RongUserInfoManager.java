package io.rong.imkit.userinfo;

import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import java.util.ArrayList;
import java.util.List;

import io.rong.common.RLog;
import io.rong.imkit.IMCenter;
import io.rong.imkit.userinfo.db.model.Group;
import io.rong.imkit.userinfo.db.model.GroupMember;
import io.rong.imkit.userinfo.db.model.User;
import io.rong.imkit.userinfo.model.GroupUserInfo;
import io.rong.imkit.utils.ExecutorHelper;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;

public class RongUserInfoManager {
    private final String TAG = RongUserInfoManager.class.getSimpleName();
    private UserDataDelegate mUserDataDelegate;
    private LocalUserDataSource mLocalDataSource;
    private UserDatabase mDatabase;
    private UserInfo mCurrentUserInfo;
    private boolean mIsUserInfoAttached;
    private boolean mIsCacheUserInfo = true;
    private boolean mIsCacheGroupInfo = true;
    private boolean mIsCacheGroupMemberInfo = true;
    private MediatorLiveData<List<User>> mAllUsers;
    private MediatorLiveData<List<Group>> mAllGroups;
    private MediatorLiveData<List<GroupMember>> mAllGroupMembers;
    private List<UserDataObserver> mUserDataObservers;

    private static class SingleTonHolder {
        static RongUserInfoManager sInstance = new RongUserInfoManager();
    }

    public static RongUserInfoManager getInstance() {
        return SingleTonHolder.sInstance;
    }

    private RongUserInfoManager() {
        mUserDataDelegate = new UserDataDelegate();
        mUserDataObservers = new ArrayList<>();
        mAllUsers = new MediatorLiveData<>();
        mAllGroups = new MediatorLiveData<>();
        mAllGroupMembers = new MediatorLiveData<>();
        IMCenter.getInstance().addOnReceiveMessageListener(new RongIMClient.OnReceiveMessageWrapperListener() {
            @Override
            public boolean onReceived(Message message, int i, boolean b, boolean b1) {
                if (message != null && message.getContent() != null && message.getContent().getUserInfo() != null
                        && RongUserInfoManager.getInstance().getUserDatabase() != null) {
                    final UserInfo userInfo = message.getContent().getUserInfo();
                    ExecutorHelper.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            RongUserInfoManager.getInstance().getUserDatabase().getUserDao().insertUser(new User(userInfo));
                        }
                    });
                }
                return false;
            }
        });
        mAllUsers.addSource(UserDatabase.getDatabaseCreated(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean value) {
                if (value) {
                    mAllUsers.addSource(mDatabase.getUserDao().getAllUsers(), new Observer<List<User>>() {
                        @Override
                        public void onChanged(List<User> users) {
                            mAllUsers.postValue(users);
                        }
                    });
                } else {
                    mAllUsers.removeSource(UserDatabase.getDatabaseCreated());
                }
            }
        });
        mAllGroups.addSource(UserDatabase.getDatabaseCreated(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean value) {
                if (value) {
                    mAllGroups.addSource(mDatabase.getGroupDao().getAllGroups(), new Observer<List<Group>>() {
                        @Override
                        public void onChanged(List<Group> groups) {
                            mAllGroups.postValue(groups);
                        }
                    });
                }
            }
        });
        mAllGroupMembers.addSource(UserDatabase.getDatabaseCreated(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean value) {
                if (value) {
                    mAllGroupMembers.addSource(mDatabase.getGroupMemberDao().getAllGroupMembers(), new Observer<List<GroupMember>>() {
                        @Override
                        public void onChanged(List<GroupMember> groupMembers) {
                            mAllGroupMembers.postValue(groupMembers);
                        }
                    });
                }
            }
        });
    }

    /**
     * /~chinese
     * ???????????????????????????????????????
     *
     * @param context
     */

    /**
     * /~english
     * Initialize and open the user information database
     *
     * @param context
     */
    public void initAndUpdateUserDataBase(Context context) {
        mDatabase = UserDatabase.openDb(context, RongIMClient.getInstance().getCurrentUserId());
        if (mDatabase != null) {
            mLocalDataSource = new LocalUserDataSource(mDatabase);
        }
    }

    /**
     * /~chinese
     * <p>
     * ???????????????????????????????????? UI ????????????????????????????????????
     * ??? ViewModel ??????????????????????????????????????????????????????
     * ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????? null ?????????
     * ???????????????????????????????????????????????????????????? {@link #refreshUserInfoCache(UserInfo)}}  ?????????????????????
     * </p>
     *
     * @param userInfoProvider ????????????????????? {@link UserDataProvider.UserInfoProvider}???
     * @param isCacheUserInfo  ??????????????? IMKit ????????????????????????<br>
     *                         ?????? App ????????? UserInfoProvider???
     *                         ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????<br>
     *                         ????????????????????????????????? true?????? IMKit ????????????????????????
     */

    /**
     * /~english
     * Set the provider of user information for UI to obtain user name and portrait information,
     * Each ViewModel will listen to the changes of user information and refresh accordingly,
     * If you shall get user information asynchronously from the server,
     * the consumer can initiate an asynchronous request in this method and then return null information,
     * After the result of the asynchronous request is returned, call {@link #refreshUserInfoCache(UserInfo)}} to refresh the user information based on the returned result.
     *
     * @param userInfoProvider User information provider {@link UserDataProvider.UserInfoProvider}.
     * @param isCacheUserInfo  Set whether user information is cached by IMKit.
     *                         If the UserInfoProvider provided by App,
     *                         Each time you shall request user data over the network instead of caching it locally, which will affect the loading speed of user information.
     *                         In this case, it is best to set this parameter to true, to cache user information by IMKit.
     */
    public void setUserInfoProvider(UserDataProvider.UserInfoProvider userInfoProvider, boolean isCacheUserInfo) {
        mUserDataDelegate.setUserInfoProvider(userInfoProvider);
        mIsCacheUserInfo = isCacheUserInfo;
    }

    public void setGroupInfoProvider(UserDataProvider.GroupInfoProvider groupInfoProvider, boolean isCacheGroupInfo) {
        mUserDataDelegate.setGroupInfoProvider(groupInfoProvider);
        mIsCacheGroupInfo = isCacheGroupInfo;
    }

    /**
     * /~chinese
     * <p>????????????????????????</p>
     * <p>???????????????????????????????????????????????????</p>
     * <p>??????????????? sdk ??????????????????????????????????????? {@link UserDataProvider.GroupUserInfoProvider#getGroupUserInfo(String, String)}
     * ????????????????????????????????? groupId, userId ??????????????????????????? {@link GroupUserInfo}???
     * ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????? null ?????????
     * ???????????????????????????????????????????????????????????? {@link #refreshGroupUserInfoCache(GroupUserInfo)} ???????????????</p>
     *
     * @param groupUserInfoProvider ??????????????????????????????
     * @param isCacheGroupUserInfo  ??????????????? IMKit ????????? GroupUserInfo???<br>
     *                              ?????? App ????????? GroupUserInfoProvider???
     *                              ???????????????????????????????????????????????????????????????????????????????????????????????????????????????<br>
     *                              ????????????????????????????????? true?????? IMKit ??????????????????
     */

    /**
     * /~english
     * Set a group member provider.
     * You can use this method to modify user nicknames in the group.
     * After setting, when the user information is displayed in the sdk interface,
     * the {@link UserDataProvider.GroupUserInfoProvider#getGroupUserInfo(String, String)} will be called back,
     * and only shall provide the corresponding user information GroupUserInfo according to the corresponding groupId, userId.
     * If you shall get user information asynchronously from the server, the consumer can initiate an asynchronous request in this method and then return null information.
     * After the result of the asynchronous request is returned, {@link #refreshGroupUserInfoCache(GroupUserInfo)} refresh information is called based on the returned result.
     *
     * @param groupUserInfoProvider Group user information provider.
     * @param isCacheGroupUserInfo  Set whether GroupUserInfo is cached by IMKit.
     *                              If the GroupUserInfoProvider is provided by App Each time you shall request data over the network instead of caching the data locally,
     *                              which will affect the loading speed of the information.
     *                              <p>
     *                              In this case, it is best to set this parameter to true, and the information is cached by IMKit.
     */
    public void setGroupUserInfoProvider(UserDataProvider.GroupUserInfoProvider groupUserInfoProvider, boolean isCacheGroupUserInfo) {
        mUserDataDelegate.setGroupUserInfoProvider(groupUserInfoProvider);
        mIsCacheGroupMemberInfo = isCacheGroupUserInfo;
    }

    public UserDatabase getUserDatabase() {
        return mDatabase;
    }

    public LiveData<List<User>> getAllUsersLiveData() {
        return mAllUsers;
    }

    public LiveData<List<Group>> getAllGroupsLiveData() {
        return mAllGroups;
    }

    public LiveData<List<GroupMember>> getAllGroupMembersLiveData() {
        return mAllGroupMembers;
    }

    public UserInfo getUserInfo(String userId) {
        if (TextUtils.isEmpty(userId)) {
            return null;
        }
        User user = null;
        if (mIsCacheUserInfo && mLocalDataSource != null) {
            user = mLocalDataSource.getUserInfo(userId, mAllUsers);
        }
        if (user == null && mUserDataDelegate != null) {
            UserInfo userInfo = mUserDataDelegate.getUserInfo(userId);
            if (userInfo != null) {
                user = new User(userInfo.getUserId(), userInfo.getName() == null ? "" : userInfo.getName(),
                        userInfo.getPortraitUri());
            }
            if (mIsCacheUserInfo && user != null && mLocalDataSource != null) {
                mLocalDataSource.refreshUserInfo(user);
            }
        }
        if (user != null) {
            UserInfo userInfo = new UserInfo(user.id, user.name == null ? "" : user.name, Uri.parse(user.portraitUrl));
            userInfo.setExtra(user.extra);
            return userInfo;
        } else {
            return null;
        }
    }

    public LiveData<User> getUserInfoLiveData(final String userId) {
        final MediatorLiveData userLiveData = new MediatorLiveData<>();
        userLiveData.addSource(UserDatabase.getDatabaseCreated(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean value) {
                if (value) {
                    userLiveData.addSource(mDatabase.getUserDao().getUserLiveData(userId), new Observer<User>() {
                        @Override
                        public void onChanged(User user) {
                            userLiveData.postValue(user);
                        }
                    });
                } else {
                    userLiveData.removeSource(UserDatabase.getDatabaseCreated());
                }
            }
        });
        return userLiveData;
    }

    public io.rong.imlib.model.Group getGroupInfo(final String groupId) {
        if (TextUtils.isEmpty(groupId)) {
            return null;
        }
        Group group = null;
        if (mIsCacheGroupInfo && mLocalDataSource != null) {
            group = mLocalDataSource.getGroupInfo(groupId);
        }
        if (group == null && mUserDataDelegate != null) {
            group = mUserDataDelegate.getGroupInfo(groupId);
            if (mIsCacheGroupInfo && group != null && mLocalDataSource != null) {
                mLocalDataSource.refreshGroupInfo(group);
            }
        }
        if (group != null) {
            RLog.d(TAG, "get group info synchronize. name:" + group.name);
            return new io.rong.imlib.model.Group(group.id, group.name, Uri.parse(group.portraitUrl));
        } else {
            RLog.d(TAG, "get group info null");
            return null;
        }
    }

    public GroupUserInfo getGroupUserInfo(final String groupId, final String userId) {
        if (TextUtils.isEmpty(groupId) || TextUtils.isEmpty(userId)) {
            return null;
        }
        GroupMember groupMember = null;
        if (mIsCacheGroupMemberInfo && mLocalDataSource != null) {
            groupMember = mLocalDataSource.getGroupUserInfo(groupId, userId, mAllGroupMembers);
        }
        if (groupMember == null && mUserDataDelegate != null) {
            groupMember = mUserDataDelegate.getGroupUserInfo(groupId, userId);
            if (mIsCacheGroupMemberInfo && groupMember != null && mLocalDataSource != null) {
                mLocalDataSource.refreshGroupUserInfo(groupMember);
            }
        }

        if (groupMember != null) {
            return new GroupUserInfo(groupId, userId, groupMember.memberName);
        } else {
            return null;
        }
    }

    /**
     * /~chinese
     * ???????????????????????????
     * ????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
     * ?????????{@link IMCenter#init(Application, String, boolean)}????????????{@link #setMessageAttachedUserInfo(boolean)}???
     * ????????????????????????????????????????????????????????????IMKit????????????????????????????????????????????????????????????????????????
     *
     * @param userInfo ?????????????????????
     */

    /**
     * /~english
     * Set the current user information.
     * If the developer does not implement a user information provider, but uses messages to carry user information,
     * you shall use this method to set the current user's information,
     * and then call {@link #setMessageAttachedUserInfo(boolean)} after {@link IMCenter#init(Application, String, boolean)},
     * so that you can carry the current user's information in each message.
     * <p>
     * IMKit will take out the user's information and refresh it to the interface when it receives the message.
     *
     * @param userInfo Current user information
     */
    public void setCurrentUserInfo(UserInfo userInfo) {
        mCurrentUserInfo = userInfo;
    }

    public UserInfo getCurrentUserInfo() {
        if (mCurrentUserInfo != null) {
            return mCurrentUserInfo;
        } else {
            return getUserInfo(RongIMClient.getInstance().getCurrentUserId());
        }
    }

    /**
     * /~chinese
     * ?????????????????????????????????????????????
     *
     * @param state ???????????????????????????true ?????????false ????????????
     */

    /**
     * /~english
     * Set whether the message body carries user information.
     *
     * @param state Whether or not to carry user information, true indicates to carry and false indicates not to carry.
     */
    public void setMessageAttachedUserInfo(boolean state) {
        mIsUserInfoAttached = state;
    }

    /**
     * /~chinese
     * ?????????????????????????????????????????????????????????????????????
     *
     * @return ????????????????????????
     */

    /**
     * /~english
     * Get the configuration of the current user about whether the message body carries user information.
     *
     * @return Whether to carry user information
     */
    public boolean getUserInfoAttachedState() {
        return mIsUserInfoAttached;
    }

    public void refreshUserInfoCache(UserInfo userInfo) {
        if (userInfo == null) {
            RLog.e(TAG, "Invalid to refresh a null user object.");
            return;
        }
        User user = new User(userInfo.getUserId(), userInfo.getName(), userInfo.getPortraitUri());
        user.extra = userInfo.getExtra();

        if (mIsCacheUserInfo && mLocalDataSource != null) {
            mLocalDataSource.refreshUserInfo(user);
        } else {
            List<User> userList = mAllUsers.getValue();
            if (userList == null) {
                userList = new ArrayList<>();
            }
            for (User temp : userList) {
                if (temp.id.equals(user.id)) {
                    userList.remove(temp);
                    userList.add(user);
                    break;
                }
            }
            mAllUsers.postValue(userList);
        }
        for (UserDataObserver observer : mUserDataObservers) {
            observer.onUserUpdate(userInfo);
        }
    }

    public void refreshGroupInfoCache(io.rong.imlib.model.Group groupInfo) {
        if (groupInfo == null) {
            RLog.e(TAG, "Invalid to refresh a null group object.");
            return;
        }
        RLog.d(TAG, "refresh Group info.");
        Group group = new Group(groupInfo.getId(), groupInfo.getName(), groupInfo.getPortraitUri() == null ? "" : groupInfo.getPortraitUri().toString());

        if (mIsCacheGroupInfo && mLocalDataSource != null) {
            mLocalDataSource.refreshGroupInfo(group);
        } else {
            List<Group> groupList = mAllGroups.getValue();
            if (groupList == null) {
                groupList = new ArrayList<>();
            }
            for (Group temp : groupList) {
                if (temp.id.equals(groupInfo.getId())) {
                    groupList.remove(temp);
                    groupList.add(group);
                    break;
                }
            }
            mAllGroups.postValue(groupList);
        }
        for (UserDataObserver observer : mUserDataObservers) {
            observer.onGroupUpdate(groupInfo);
        }
    }

    public void refreshGroupUserInfoCache(GroupUserInfo groupUserInfo) {
        if (groupUserInfo == null) {
            RLog.e(TAG, "Invalid to refresh a null groupUserInfo object.");
            return;
        }
        GroupMember groupMember = new GroupMember(groupUserInfo.getGroupId(), groupUserInfo.getUserId(), groupUserInfo.getNickname());
        if (mIsCacheGroupMemberInfo && mLocalDataSource != null) {
            mLocalDataSource.refreshGroupUserInfo(groupMember);
        } else {
            List<GroupMember> groupMemberList = mAllGroupMembers.getValue();
            if (groupMemberList == null) {
                groupMemberList = new ArrayList<>();
            }
            for (GroupMember temp : groupMemberList) {
                if (temp.groupId.equals(groupMember.groupId) && temp.userId.equals(groupMember.userId)) {
                    groupMemberList.remove(temp);
                    groupMemberList.add(groupMember);
                    break;
                }
            }
            mAllGroupMembers.postValue(groupMemberList);
        }
        for (UserDataObserver observer : mUserDataObservers) {
            observer.onGroupUserInfoUpdate(groupUserInfo);
        }
    }

    public void addUserDataObserver(UserDataObserver observer) {
        mUserDataObservers.add(observer);
    }

    public void removeUserDataObserver(UserDataObserver observer) {
        mUserDataObservers.remove(observer);
    }

    public interface UserDataObserver {
        void onUserUpdate(UserInfo info);

        void onGroupUpdate(io.rong.imlib.model.Group group);

        void onGroupUserInfoUpdate(GroupUserInfo groupUserInfo);
    }
}
