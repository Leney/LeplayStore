package com.xd.leplay.store.gui.personal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.xd.base.util.DLog;
import com.xd.leplay.store.Constants;
import com.xd.leplay.store.DataCollectionConstant;
import com.xd.leplay.store.LeplayApplication;
import com.xd.leplay.store.R;
import com.xd.leplay.store.control.ConstantManager;
import com.xd.leplay.store.control.DataCollectionManager;
import com.xd.leplay.store.control.ImageLoaderManager;
import com.xd.leplay.store.control.LoginUserInfoManager;
import com.xd.leplay.store.control.OnWeiXinResponsLinstener;
import com.xd.leplay.store.control.WeiXinAPIManager;
import com.xd.leplay.store.gui.login.LoginActivity;
import com.xd.leplay.store.gui.main.BaseActivity;
import com.xd.leplay.store.model.ConstantInfo;
import com.xd.leplay.store.model.LoginedUserInfo;
import com.xd.leplay.store.model.TreasureInfo;
import com.xd.leplay.store.model.proto.Packet.RspPacket;
import com.xd.leplay.store.model.proto.Uac.ReqLogOut;
import com.xd.leplay.store.model.proto.Uac.ReqSetUserInfo;
import com.xd.leplay.store.model.proto.Uac.RspSetUserInfo;
import com.xd.leplay.store.util.DisplayUtil;
import com.xd.leplay.store.util.ToolsUtil;
import com.xd.leplay.store.view.BottomDialog;
import com.xd.leplay.store.view.CenterDialog;
import com.xd.leplay.store.view.LoadingDialog;
import com.xd.leplay.store.view.PasswordTextWatcher;
import com.google.protobuf.ByteString;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

/**
 * 个人中心界面
 * 
 * @author lilijun
 *
 */
public class PersonalCenterActivity extends BaseActivity implements
		OnClickListener, OnWeiXinResponsLinstener
{
	private final String TAG = "PersonalCenterActivity";

	/** 顶部 头像、名称显示区域 */
	private LinearLayout userInfoLay;

	/** 用户头像 */
	private ImageView userIcon;

	/** 用户名称 、金币数、我的等级、已提现 */
	private TextView userName, userCoin, myGrade, getMoney;

	/** 等级经验进度条 */
	private ProgressBar[] gradeProgresses = new ProgressBar[5];

	/** 6个等级的名称 */
	private TextView[] gradeItemTexts = new TextView[6];

	/** 6个等级经验进度条上的圆形图标 */
	private ImageView[] gradeItemImgs = new ImageView[6];

	/** 更改绑定手机、绑定微信、更改昵称、更改性别 */
	private FrameLayout changePhoneLay, bindWeiXinLay, changeNickNameLay,
			changeSexLay;

	/** 修改密码、退出帐号 */
	/* private TextView changePwdText, exitAccountText; */

	/** 手机号、昵称、性别、微信绑定状态 */
	private TextView phone, nickName, sex, wxBindStatus;

	private ImageLoaderManager imageLoaderManager = null;

	private DisplayImageOptions options = null;

	/** 退出登录的请求TAG */
	private final String EXIT_LOGIN_REQUEST_TAG = "ReqLogOut";

	/** 退出登录的响应TAG */
	private final String EXIT_LOGIN_RSPONSE_TAG = "RspLogOut";

	/** 修改用户信息的请求TAG */
	private final String MODIFY_USER_INFO_REQUEST_TAG = "ReqSetUserInfo";

	/** 修改用户信息的响应TAG */
	private final String MODIFY_USER_INFO_RSPONSE_TAG = "RspSetUserInfo";

	private LoadingDialog loadingDialog = null;

	private LoadingDialog uploadPicDialog = null;

	/** 调用相册标识 */
	private static final int GET_LOCAL_PHOTO_PIC = 1;

	/** 裁剪图片标识 */
	private static final int CROP_PHOTO_PIC = 2;

	/** 用来标识请求照相功能的activity */
	private static final int CAMERA_WITH_DATA = 3;

	/** 相机裁剪图片标识 */
	private static final int CROP_CAMER_PCI = 4;

	/** 更改头像时，拍照后保存到本地的图片路径 */
	private String CAMERA_PIC_PATH = Environment.getExternalStorageDirectory()
			+ "/camera.jpg";

	/** 裁剪后的图片保存位置(文件夹) */
	public static final File PHOTO_DIR = new File(
			Environment.getExternalStorageDirectory() + "/DCIM/Camera");

	private LoginedUserInfo userInfo = null;

	/** 用户的财富信息 */
	private TreasureInfo treasureInfo = null;

	private ConstantManager constantManager = null;

	private ConstantInfo constantInfo = null;

	// 匹配非表情符号的正则表达式
	// private String regx =
	// "^([a-z]|[A-Z]|[0-9]|[\u2E80-\u9FFF]){3,}|[A-Z0-9a-z`~!@#$%^&*()+=|{}':;',.<>/?~！@#￥%……& amp;*（）——+|{}【】‘；：”“’。，、？]";
	private String regx = "^([a-z]|[A-Z]|[0-9]|[\u4e00-\u9fff])+$|[A-Z0-9a-z`~!@#$%^&*()+=|{}':;',.<>/?~！@#￥%……& amp;*（）——+|{}【】‘；：”“’。，、？]";

	/** 显示换行等级的字符串id */
	private int playerNames[] =
	{ R.string.player1, R.string.player2, R.string.player3, R.string.player4,
			R.string.player5, R.string.player6 };

	@Override
	protected void initView()
	{
		if (!LoginUserInfoManager.getInstance().isHaveUserLogin())
		{
			finish();
			return;
		}
		action = DataCollectionManager.getAction(DataCollectionManager
				.getIntentDataCollectionAction(getIntent()),
				DataCollectionConstant.DATA_COLLECTION_PERSIONAL_VALUE);
		DataCollectionManager.getInstance().addRecord(action);

		titleView.setTitleName(getResources().getString(
				R.string.personal_center));
		titleView.setRightLayVisible(false);
		titleView.setBottomLineVisible(true);

		loadingView.setVisibilyView(false);
		imageLoaderManager = ImageLoaderManager.getInstance();
		options = DisplayUtil.getUserIconImageLoaderOptions();

		userInfo = LoginUserInfoManager.getInstance().getLoginedUserInfo();
		treasureInfo = userInfo.getTreasureInfo();
		constantManager = ConstantManager.getInstance();
		constantInfo = constantManager.getConstantInfo();

		setCenterView(R.layout.personal_layout);

		loadingDialog = new LoadingDialog(PersonalCenterActivity.this,
				getResources().getString(R.string.modifing));
		loadingDialog.setCanceledOnTouchOutside(false);

		uploadPicDialog = new LoadingDialog(PersonalCenterActivity.this,
				getResources().getString(R.string.uploading));
		uploadPicDialog.setCanceledOnTouchOutside(false);

		// 设置微信授权登录响应事件对象
		WeiXinAPIManager.getInstance().setLinstener(this);

		userInfoLay = (LinearLayout) findViewById(R.id.personal_user_info_lay);
		userInfoLay.setOnClickListener(this);
		userIcon = (ImageView) findViewById(R.id.personal_user_icon);
		userName = (TextView) findViewById(R.id.personal_user_name);
		// userCoin = (TextView) findViewById(R.id.personal_user_coins);
		myGrade = (TextView) findViewById(R.id.personal_my_grade_text);
		getMoney = (TextView) findViewById(R.id.personal_get_money);
		gradeProgresses[0] = (ProgressBar) findViewById(R.id.personal_level_progress_1);
		gradeProgresses[1] = (ProgressBar) findViewById(R.id.personal_level_progress_2);
		gradeProgresses[2] = (ProgressBar) findViewById(R.id.personal_level_progress_3);
		gradeProgresses[3] = (ProgressBar) findViewById(R.id.personal_level_progress_4);
		gradeProgresses[4] = (ProgressBar) findViewById(R.id.personal_level_progress_5);
		gradeItemTexts[0] = (TextView) findViewById(R.id.personal_grade_player1);
		gradeItemTexts[1] = (TextView) findViewById(R.id.personal_grade_player2);
		gradeItemTexts[2] = (TextView) findViewById(R.id.personal_grade_player3);
		gradeItemTexts[3] = (TextView) findViewById(R.id.personal_grade_player4);
		gradeItemTexts[4] = (TextView) findViewById(R.id.personal_grade_player5);
		gradeItemTexts[5] = (TextView) findViewById(R.id.personal_grade_player6);
		gradeItemImgs[0] = (ImageView) findViewById(R.id.personal_grade_img1);
		gradeItemImgs[1] = (ImageView) findViewById(R.id.personal_grade_img2);
		gradeItemImgs[2] = (ImageView) findViewById(R.id.personal_grade_img3);
		gradeItemImgs[3] = (ImageView) findViewById(R.id.personal_grade_img4);
		gradeItemImgs[4] = (ImageView) findViewById(R.id.personal_grade_img5);
		gradeItemImgs[5] = (ImageView) findViewById(R.id.personal_grade_img6);

		myGrade.setText(treasureInfo.getLevalName());
		getMoney.setText("￥" + treasureInfo.getWithdrawMoney());

		if (!constantInfo.getLevelInfos().isEmpty())
		{
			for (int i = 0; i < gradeItemTexts.length; i++)
			{
				String name = constantInfo.getLevelInfos().get(i + 1)
						.getLevelName();
				if (name.length() >= 2)
				{
					// 将等级名称从第三个字符开始换行
					name = name.substring(0, 2) + "\n" + name.substring(2);
					gradeItemTexts[i].setText(name);
				}
			}
		} else
		{
			for (int i = 0; i < gradeItemTexts.length; i++)
			{
				gradeItemTexts[i].setText(getResources().getString(
						playerNames[i]));
			}
		}

		// 设置每个进度条的最大值
		// if (constantManager.isHaveConstantInfo())
		// {
		for (int i = 0; i < gradeProgresses.length; i++)
		{
			gradeProgresses[i].setMax(constantInfo.getLevelInfos().get(i + 1)
					.getEndAmount()
					- constantInfo.getLevelInfos().get(i + 1).getStartAmount());
		}
		// } else
		// {
		// constantInfo = new ConstantInfo();
		// constantInfo.getLevelInfos().putAll(ConstantManager.LEVEL_INFOS);
		// for (int i = 0; i < gradeProgresses.length; i++)
		// {
		// gradeProgresses[i].setMax(constantInfo.getLevelInfos()
		// .get(i + 1).getEndAmount()
		// - ConstantManager.LEVEL_INFOS.get(i + 1)
		// .getStartAmount());
		// }
		// }

		switch (userInfo.getTreasureInfo().getLevalNo())
		{
		case 1:
			gradeProgresses[0].setProgress((int) Math.ceil(treasureInfo
					.getWithdrawMoney()));
			break;
		case 2:
			gradeProgresses[0].setProgress(gradeProgresses[0].getMax());
			// 得到最后一段进度条的进度值
			gradeProgresses[1].setProgress((int) Math.ceil(treasureInfo
					.getWithdrawMoney() - gradeProgresses[0].getMax()));
			break;
		case 3:
			gradeProgresses[0].setProgress(gradeProgresses[0].getMax());
			gradeProgresses[1].setProgress(gradeProgresses[1].getMax());
			gradeProgresses[2].setProgress((int) Math.ceil(treasureInfo
					.getWithdrawMoney()
					- gradeProgresses[0].getMax()
					- gradeProgresses[1].getMax()));
			break;
		case 4:
			gradeProgresses[0].setProgress(gradeProgresses[0].getMax());
			gradeProgresses[1].setProgress(gradeProgresses[1].getMax());
			gradeProgresses[2].setProgress(gradeProgresses[2].getMax());
			gradeProgresses[3]
					.setProgress((int) Math.ceil(treasureInfo
							.getWithdrawMoney()
							- gradeProgresses[0].getMax()
							- gradeProgresses[1].getMax()
							- gradeProgresses[2].getMax()));
			break;
		case 5:
			gradeProgresses[0].setProgress(gradeProgresses[0].getMax());
			gradeProgresses[1].setProgress(gradeProgresses[1].getMax());
			gradeProgresses[2].setProgress(gradeProgresses[2].getMax());
			gradeProgresses[3].setProgress(gradeProgresses[3].getMax());
			gradeProgresses[4].setProgress((int) Math.ceil(treasureInfo
					.getWithdrawMoney()
					- gradeProgresses[0].getMax()
					- gradeProgresses[1].getMax()
					- gradeProgresses[2].getMax()
					- gradeProgresses[3].getMax()));
			break;
		case 6:
			gradeProgresses[0].setProgress(gradeProgresses[0].getMax());
			gradeProgresses[1].setProgress(gradeProgresses[1].getMax());
			gradeProgresses[2].setProgress(gradeProgresses[2].getMax());
			gradeProgresses[3].setProgress(gradeProgresses[3].getMax());
			gradeProgresses[4].setProgress(gradeProgresses[4].getMax());
			break;
		}
		for (int i = 0; i < gradeItemImgs.length; i++)
		{
			if (i + 1 <= treasureInfo.getLevalNo())
			{
				// 显示等级达到的状态
				gradeItemTexts[i]
						.setBackgroundResource(R.drawable.circle_light_red_bg_shape);
				gradeItemImgs[i].setImageResource(R.drawable.personal_lv_comed);
			} else
			{
				// 显示等级未达到的状态
				gradeItemTexts[i]
						.setBackgroundResource(R.drawable.circle_deep_gray_bg_shape);
				gradeItemImgs[i]
						.setImageResource(R.drawable.personal_lv_uncome);
			}
		}

		changePhoneLay = (FrameLayout) findViewById(R.id.personal_change_phone_num_lay);
		changePhoneLay.setOnClickListener(this);
		bindWeiXinLay = (FrameLayout) findViewById(R.id.personal_bind_WeiXin_Lay);
		bindWeiXinLay.setOnClickListener(this);
		changeNickNameLay = (FrameLayout) findViewById(R.id.personal_change_nick_name_lay);
		changeNickNameLay.setOnClickListener(this);
		changeSexLay = (FrameLayout) findViewById(R.id.personal_change_sex_lay);
		changeSexLay.setOnClickListener(this);
		/*
		 * changePwdText = (TextView)
		 * findViewById(R.id.personal_change_pwd_text);
		 * changePwdText.setOnClickListener(this); exitAccountText = (TextView)
		 * findViewById(R.id.personal_exit_account_text);
		 * exitAccountText.setOnClickListener(this);
		 */
		phone = (TextView) findViewById(R.id.personal_change_phone_num_text);
		nickName = (TextView) findViewById(R.id.personal_change_nick_name_text);
		sex = (TextView) findViewById(R.id.personal_change_sex_text);

		wxBindStatus = (TextView) findViewById(R.id.weixin_bind_status);
		setData();

		IntentFilter filter = new IntentFilter(
				Constants.ACTION_ACCOUNT_HAVE_MODIFY);
		registerReceiver(receiver, filter);

	}

	/**
	 * 设置相关信息
	 */
	private void setData()
	{
		LoginedUserInfo userInfo = LoginUserInfoManager.getInstance()
				.getLoginedUserInfo();
		if (userInfo == null)
		{
			finish();
		} else
		{
			imageLoaderManager.displayImage(userInfo.getIconUrl(), userIcon,
					options);
			if (!"".equals(userInfo.getNickName()))
			{
				userName.setText(userInfo.getNickName());
				nickName.setText(userInfo.getNickName());
			} else
			{
				userName.setText(userInfo.getAccount());
				nickName.setText(getResources().getString(R.string.without));
			}
			/*
			 * userCoin.setText(userInfo.getTreasureInfo().getCoinNum() +
			 * getResources().getString(R.string.coins));
			 */
			if (userInfo.getSex() == 0)
			{
				// 女
				sex.setText(getResources().getString(R.string.woman));
			} else if (userInfo.getSex() == 1)
			{
				// 男
				sex.setText(getResources().getString(R.string.man));
			} else
			{
				// 未知
				sex.setText(getResources().getString(R.string.unknow));
			}
			if (userInfo.getWxUnionId() == null
					|| userInfo.getWxUnionId().equals(""))
			{
				wxBindStatus.setText(getResources().getString(
						R.string.click_bind));
			} else
				wxBindStatus.setText(getResources()
						.getString(R.string.is_bound));
			if (userInfo.getPhone() == null || userInfo.getPhone().equals(""))
			{
				phone.setText(getResources().getString(R.string.click_bind));
			} else
				phone.setText(userInfo.getPhone());
		}
	}

	@Override
	protected void loadDataSuccess(RspPacket rspPacket)
	{
		super.loadDataSuccess(rspPacket);
		List<String> actions = rspPacket.getActionList();
		for (String action : actions)
		{
			if (EXIT_LOGIN_RSPONSE_TAG.equals(action))
			{
				// 退出登录
				// try
				// {
				// RspLogOut rspLogOut = RspLogOut.parseFrom(rspPacket
				// .getParams(0));
				// if (rspLogOut.getRescode() == 0)
				// {
				// 不管服务器有没有退出成功 前端都认为退出成功
				// 退出登录
				LoginUserInfoManager.getInstance().exitLogin();
				finish();
				// } else
				// {
				// Toast.makeText(this, rspLogOut.getResmsg(),
				// Toast.LENGTH_SHORT).show();
				// }
				// } catch (Exception e)
				// {
				// DLog.e(TAG, "loadDataSuccess()#Excepton:", e);
				// }
			} else if (MODIFY_USER_INFO_RSPONSE_TAG.equals(action))
			{
				// 修改用户信息
				parseModifyUserInfoResultData(rspPacket.getParams(0));
				setData();
				loadingDialog.dismiss();
				uploadPicDialog.dismiss();
				// 发送用户的相关信息发生了改变的广播
				sendBroadcast(new Intent(
						Constants.ACTION_LOGINED_USER_INFO_HAVE_MODIFY));
			}
		}
	}

	@Override
	protected void loadDataFailed(RspPacket rspPacket)
	{
		super.loadDataFailed(rspPacket);
		loadingDialog.dismiss();
		for (String actionNet : rspPacket.getActionList())
		{
			if (EXIT_LOGIN_REQUEST_TAG.equals(actionNet))
			{
				// 不管服务器有没有退出成功 前端都认为退出成功
				// 退出登录
				LoginUserInfoManager.getInstance().exitLogin();
				finish();
			}
		}
	}

	@Override
	protected void netError(String[] actions)
	{
		super.netError(actions);
		loadingDialog.dismiss();
		for (String actionNet : actions)
		{
			if (EXIT_LOGIN_REQUEST_TAG.equals(actionNet))
			{
				// 不管服务器有没有退出成功 前端都认为退出成功
				// 退出登录
				LoginUserInfoManager.getInstance().exitLogin();
				finish();
			}
		}
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.personal_user_info_lay:
			// 用户图像、昵称显示区域(更换头像)
			final BottomDialog changeIconDialog = new BottomDialog(
					PersonalCenterActivity.this);
			changeIconDialog.show();
			changeIconDialog.setTitleName(getResources().getString(
					R.string.change_user_icon));
			changeIconDialog.setBottomBtnsLayVisible(false);
			View changeIconView = View.inflate(PersonalCenterActivity.this,
					R.layout.change_user_icon_dialog, null);
			LinearLayout cameraBtn = (LinearLayout) changeIconView
					.findViewById(R.id.change_user_icon_camera_btn);
			LinearLayout photosBtn = (LinearLayout) changeIconView
					.findViewById(R.id.change_user_icon_photo_btn);
			cameraBtn.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					// 拍照
					intentCameraActivity();
					changeIconDialog.dismiss();
				}
			});
			photosBtn.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					// 相册
					// intentLocalPhotoPicActivity();
					Intent intent = new Intent(Intent.ACTION_PICK, null);
					intent.setDataAndType(
							MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
							"image/*");
					startActivityForResult(intent, GET_LOCAL_PHOTO_PIC);
					changeIconDialog.dismiss();
				}
			});
			changeIconDialog.setCenterView(changeIconView);
			String changeIconAction = DataCollectionManager
					.getAction(
							action,
							DataCollectionConstant.DATA_COLLECTION_CLICK_PERSONAL_CENTER_CHANGE_ICON_VALUE);
			// 自己后台的数据采集
			DataCollectionManager.getInstance().addRecord(changeIconAction);
			// 添加友盟的事件点击数据采集
			DataCollectionManager
					.getInstance()
					.addYouMengEventRecord(
							PersonalCenterActivity.this,
							action,
							DataCollectionConstant.DATA_COLLECTION_CLICK_PERSONAL_CENTER_CHANGE_ICON_VALUE,
							null);
			break;
		case R.id.personal_change_phone_num_lay:
			// 更改绑定手机
			/*
			 * if (!"".equals(LoginUserInfoManager.getInstance()
			 * .getLoginedUserInfo().getPhone()) &&
			 * LoginUserInfoManager.getInstance().getLoginedUserInfo()
			 * .getPhone().length() == 11) {
			 */
			// 跳转到更改手机号码获取验证码的界面
			ModifyPhoneGetCodeActivity.startModifyPhoneGetCodeActivity(
					PersonalCenterActivity.this, action);
			String changePhoneAction = DataCollectionManager
					.getAction(
							action,
							DataCollectionConstant.DATA_COLLECTION_CLICK_PERSONAL_CENTER_CHANGE_PHONE_VALUE);
			// 自己后台的数据采集
			DataCollectionManager.getInstance().addRecord(changePhoneAction);
			// 添加友盟的事件点击数据采集
			DataCollectionManager
					.getInstance()
					.addYouMengEventRecord(
							PersonalCenterActivity.this,
							action,
							DataCollectionConstant.EVENT_ID_CLICK_PERSONAL_CENTER_CHANGE_PHONE_VALUE,
							null);
			// }
			break;
		case R.id.personal_bind_WeiXin_Lay:
			// 绑定微信
			if (userInfo.getWxUnionId() == null
					|| userInfo.getWxUnionId().equals(""))
			{
				loadingDialog.show();
				String bindWXAction = DataCollectionManager
						.getAction(
								action,
								DataCollectionConstant.DATA_COLLECTION_CLICK_PERSONAL_CENTER_BIND_WX_VALUE);
				// 自己后台的数据采集
				DataCollectionManager.getInstance().addRecord(bindWXAction);
				// 添加友盟的事件点击数据采集
				DataCollectionManager
						.getInstance()
						.addYouMengEventRecord(
								PersonalCenterActivity.this,
								action,
								DataCollectionConstant.EVENT_ID_CLICK_PERSONAL_CENTER_BIND_WX_VALUE,
								null);
				WeiXinAPIManager.getInstance().wxLogin2("1");
				// WeiXinAPIManager.getInstance().wxLogin(
				// new OnWeiXinResponsLinstener()
				// {
				//
				// @Override
				// public void onSuccess()
				// {
				// runOnUiThread(new Runnable()
				// {
				//
				// @Override
				// public void run()
				// {
				// loadingDialog.dismiss();
				// setData();
				// // 发送用户的相关信息发生了改变的广播
				// sendBroadcast(new Intent(
				// Constants.ACTION_LOGINED_USER_INFO_HAVE_MODIFY));
				// }
				// });
				//
				// }
				//
				// @Override
				// public void onError(final String msg)
				// {
				// runOnUiThread(new Runnable()
				// {
				//
				// @Override
				// public void run()
				// {
				// loadingDialog.dismiss();
				// if ("-1".equals(msg))
				// {
				// // userToken错误
				// // 退出登录
				// LoginUserInfoManager.getInstance()
				// .exitLogin();
				// Toast.makeText(
				// PersonalCenterActivity.this,
				// getResources().getString(
				// R.string.re_login),
				// Toast.LENGTH_SHORT).show();
				// LoginActivity
				// .startLoginActivity(
				// PersonalCenterActivity.this,
				// action);
				// finish();
				// } else
				// {
				// Toast.makeText(
				// PersonalCenterActivity.this,
				// msg, Toast.LENGTH_SHORT)
				// .show();
				// }
				// }
				//
				// });
				// }
				//
				// @Override
				// public void onSubmitData()
				// {
				// runOnUiThread(new Runnable()
				// {
				//
				// @Override
				// public void run()
				// {
				// loadingDialog.show();
				// }
				// });
				// };
				// }, "1");

			}
			break;
		case R.id.personal_change_nick_name_lay:
			// 更改昵称
			final CenterDialog modifyNickNameDialog = new CenterDialog(
					PersonalCenterActivity.this);
			modifyNickNameDialog.show();
			modifyNickNameDialog.setTitleName(getResources().getString(
					R.string.modify_nick_name));
			View nickNameView = View.inflate(PersonalCenterActivity.this,
					R.layout.modify_nick_name_dialog, null);
			nickNameView
					.setBackgroundResource(R.drawable.loading_dialog_bottom_bg_shape);
			final EditText nickNameInput = (EditText) nickNameView
					.findViewById(R.id.modify_nick_name_edit);
			// 正则匹配是否是表情符号
			nickNameInput.addTextChangedListener(new PasswordTextWatcher(
					nickNameInput, regx)
			{
			});
			nickNameInput.setHintTextColor(getResources().getColor(
					R.color.divider_color));
			nickNameInput.setText(LoginUserInfoManager.getInstance()
					.getLoginedUserInfo().getNickName());
			Button nickNameBtn = (Button) nickNameView
					.findViewById(R.id.modify_nick_name_submit_btn);
			nickNameBtn.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					LoginedUserInfo userInfo = LoginUserInfoManager
							.getInstance().getLoginedUserInfo();

					String changeNickName = nickNameInput.getText().toString()
							.trim();
					if ("".equals(changeNickName))
					{
						Toast.makeText(
								PersonalCenterActivity.this,
								getResources().getString(
										R.string.input_nick_name),
								Toast.LENGTH_SHORT).show();
						return;
					}

					if (changeNickName.equals(userInfo.getNickName()))
					{
						Toast.makeText(
								PersonalCenterActivity.this,
								getResources().getString(
										R.string.change_nick_name_same),
								Toast.LENGTH_SHORT).show();
						return;
					}
					doLoadData(
							Constants.UAC_API_URL,
							new String[]
							{ MODIFY_USER_INFO_REQUEST_TAG },
							new ByteString[]
							{ getModifyUserNickNameRequestData(
									userInfo.getUserId(),
									userInfo.getUserToken(), changeNickName) },
							"");
					modifyNickNameDialog.dismiss();
					loadingDialog.show();
				}
			});
			modifyNickNameDialog.setCenterView(nickNameView);
			String changeNickAction = DataCollectionManager
					.getAction(
							action,
							DataCollectionConstant.DATA_COLLECTION_CLICK_PERSONAL_CENTER_CHANGE_NICK_VALUE);
			// 自己后台的数据采集
			DataCollectionManager.getInstance().addRecord(changeNickAction);
			// 添加友盟的事件点击数据采集
			DataCollectionManager
					.getInstance()
					.addYouMengEventRecord(
							PersonalCenterActivity.this,
							action,
							DataCollectionConstant.EVENT_ID_CLICK_PERSONAL_CENTER_CHANGE_NICK_VALUE,
							null);
			break;
		case R.id.personal_change_sex_lay:
			// 更改性别
			final CenterDialog modifySexDialog = new CenterDialog(
					PersonalCenterActivity.this);
			modifySexDialog.show();
			modifySexDialog.setTitleName(getResources().getString(
					R.string.modify_sex));
			View view = View.inflate(PersonalCenterActivity.this,
					R.layout.modify_sex_dialog, null);
			view.setBackgroundResource(R.drawable.loading_dialog_bottom_bg_shape);
			final RadioGroup group = (RadioGroup) view
					.findViewById(R.id.modify_sex_radio_group);
			Button submitBtn = (Button) view
					.findViewById(R.id.modify_sex_submit_btn);
			RadioButton leftRadioButton = (RadioButton) view
					.findViewById(R.id.modify_sex_radio_left);
			RadioButton rightRadioButton = (RadioButton) view
					.findViewById(R.id.modify_sex_radio_right);
			if (userInfo.getSex() == 0)
			{
				// 女
				rightRadioButton.setChecked(true);
			} else
			{
				// 男
				leftRadioButton.setChecked(true);
			}
			submitBtn.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					int sex = -1;
					if (group.getCheckedRadioButtonId() == R.id.modify_sex_radio_left)
					{
						// 男
						sex = 1;
					} else
					{
						// 女
						sex = 0;
					}
					LoginedUserInfo userInfo = LoginUserInfoManager
							.getInstance().getLoginedUserInfo();
					doLoadData(
							Constants.UAC_API_URL,
							new String[]
							{ MODIFY_USER_INFO_REQUEST_TAG },
							new ByteString[]
							{ getModifyUserSexRequestData(userInfo.getUserId(),
									userInfo.getUserToken(), sex) }, "");
					modifySexDialog.dismiss();
					loadingDialog.show();
				}
			});
			modifySexDialog.setCenterView(view);
			break;
		/*
		 * case R.id.personal_change_pwd_text: // 修改密码
		 * ModifyPwdActivity.startModifyPwdActivity(
		 * PersonalCenterActivity.this, action); break; case
		 * R.id.personal_exit_account_text: // 退出帐号 final LoginedUserInfo
		 * userInfo = LoginUserInfoManager.getInstance() .getLoginedUserInfo();
		 * final BottomDialog dialog = new BottomDialog(this); dialog.show();
		 * dialog.setTitleName(getResources().getString(R.string.exit_account));
		 * if (!"".equals(userInfo.getNickName())) {
		 * dialog.setCenterMsg(String.format(
		 * getResources().getString(R.string.sure_exit),
		 * userInfo.getNickName())); } else { dialog.setCenterMsg(String.format(
		 * getResources().getString(R.string.sure_exit),
		 * userInfo.getAccount())); }
		 * 
		 * dialog.setLeftBtnOnclickLinstener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { dialog.dismiss(); doLoadData(
		 * Constants.UAC_API_URL, new String[] { EXIT_LOGIN_REQUEST_TAG }, new
		 * ByteString[] { getLoginOutRequestData(userInfo.getUserId(),
		 * userInfo.getUserToken()) }, ""); } });
		 * 
		 * dialog.setRightOnclickLinstener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { dialog.dismiss(); } });
		 * 
		 * break;
		 */
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (resultCode != RESULT_OK)
			return;
		switch (requestCode)
		{
		case GET_LOCAL_PHOTO_PIC:
			// 调用相册返回
			if (data != null)
			{
				// Bitmap returnBitmap = data.getParcelableExtra("data");
				Uri uri = data.getData();
				DLog.i("lilijun", "uri------>>>" + uri);
				if (uri != null)
				{
					startPhotoZoom(this, uri, CROP_PHOTO_PIC);
				} else
				{
					Toast.makeText(PersonalCenterActivity.this,
							getResources().getString(R.string.get_pic_failed),
							Toast.LENGTH_SHORT).show();
				}
				// Bitmap returnBitmap = getBitmapFromUri(uri, this);
				// startPhotoZoom(this, uri, 55);

				// DLog.i("lilijun", "returnBitmap==null------------>>>"
				// + (returnBitmap == null));
				// long size = ToolsUtil.getBitmapSize(returnBitmap);
				// if (size < 1024 * 300)
				// {
				// uploadPicDialog.show();
				// // 图片大小小于300KB 开始上传图片
				// uploadPic(Constants.UPLOAD_PIC_URL, returnBitmap);
				// } else
				// {
				// // 图片大于300KB
				// Toast.makeText(this,
				// getResources().getString(R.string.too_big_pic),
				// Toast.LENGTH_SHORT).show();
				// }
			}
			break;
		case CAMERA_WITH_DATA:
			// 调用相机拍照返回之后，再去裁剪相机所拍摄的照片
			cropPhotoPic(data);
			break;
		case CROP_CAMER_PCI:
			// 相机裁剪返回
			Bitmap returnCameraBitmap = data.getParcelableExtra("data");
			if (returnCameraBitmap != null)
			{
				long size = ToolsUtil.getBitmapSize(returnCameraBitmap);
				if (size < 1024 * 300)
				{
					uploadPicDialog.show();
					// 图片大小小于300KB 开始上传图片
					uploadPic(Constants.UPLOAD_PIC_URL, returnCameraBitmap);
				} else
				{
					// 图片大于300KB
					Toast.makeText(this,
							getResources().getString(R.string.too_big_pic),
							Toast.LENGTH_SHORT).show();
				}
			} else
			{
				// 裁剪失败
				Toast.makeText(this,
						getResources().getString(R.string.crop_pic_failed),
						Toast.LENGTH_SHORT).show();
			}
			break;
		case CROP_PHOTO_PIC:
			// 裁剪图片返回
			DLog.i("lilijun", "裁剪返回！！！@@！@！！！");
			Uri uri = data.getData();
			if (uri != null)
			{
				Bitmap returnBitmap = getBitmapFromUri(uri, this);
				DLog.i("lilijun", "returnBitmap==null------------>>>"
						+ (returnBitmap == null));
				if (returnBitmap != null)
				{
					long size = ToolsUtil.getBitmapSize(returnBitmap);
					if (size < 1024 * 300)
					{
						uploadPicDialog.show();
						// 图片大小小于300KB 开始上传图片
						uploadPic(Constants.UPLOAD_PIC_URL, returnBitmap);
					} else
					{
						// 图片大于300KB
						Toast.makeText(this,
								getResources().getString(R.string.too_big_pic),
								Toast.LENGTH_SHORT).show();
					}
				} else
				{
					// 裁剪失败
					Toast.makeText(this,
							getResources().getString(R.string.crop_pic_failed),
							Toast.LENGTH_SHORT).show();
				}
			} else
			{
				// 裁剪失败
				Toast.makeText(this,
						getResources().getString(R.string.crop_pic_failed),
						Toast.LENGTH_SHORT).show();
			}

			break;

		}
	}

	private Bitmap getBitmapFromUri(Uri uri, Context mContext)
	{
		try
		{
			// 读取uri所在的图片
			Bitmap bitmap = MediaStore.Images.Media.getBitmap(
					mContext.getContentResolver(), uri);
			return bitmap;
		} catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 上传头像到服务器
	 * 
	 * @param bitmap
	 */
	private void uploadPic(String url, Bitmap bitmap)
	{
		FormEncodingBuilder builder = new FormEncodingBuilder();
		builder.add("file_buffer", ToolsUtil.bitmapToString(bitmap));
		builder.add("file_ext", "png");
		// 构建请求
		Request request = new Request.Builder().url(url)// 地址
				.post(builder.build())// 添加请求体
				.build();
		LeplayApplication.getInstance().getHttpClient().newCall(request)
				.enqueue(new Callback()
				{
					@Override
					public void onResponse(final Response response)
							throws IOException
					{
						final String url = response.body().string();
						DLog.i("lilijun", "上传图片返回数据-url------>>>" + url);
						runOnUiThread(new Runnable()
						{
							@Override
							public void run()
							{
								if (url != null && !"".equals(url)
										&& url.startsWith("http://"))
								{
									// 上传成功
									// 提交用户头像的路径到服务器
									doLoadData(
											Constants.UAC_API_URL,
											new String[]
											{ MODIFY_USER_INFO_REQUEST_TAG },
											new ByteString[]
											{ getModifyUserPicRequestData(
													userInfo.getUserId(),
													userInfo.getUserToken(),
													url) }, "");
								} else
								{
									// 上传失败
									Toast.makeText(
											PersonalCenterActivity.this,
											getResources().getString(
													R.string.upload_failed),
											Toast.LENGTH_SHORT).show();
									uploadPicDialog.dismiss();
								}
							}
						});
					}

					@Override
					public void onFailure(Request request, IOException e)
					{
						DLog.i("lilijun", "上传图片失败！");
						runOnUiThread(new Runnable()
						{
							public void run()
							{
								Toast.makeText(
										PersonalCenterActivity.this,
										getResources().getString(
												R.string.upload_failed),
										Toast.LENGTH_SHORT).show();
								uploadPicDialog.dismiss();
							}
						});
					}
				});
	}

	/**
	 * 获取退出登录的请求数据
	 * 
	 * @param userId
	 * @param userToken
	 * @return
	 */
	private ByteString getLoginOutRequestData(int userId, String userToken)
	{
		ReqLogOut.Builder builder = ReqLogOut.newBuilder();
		builder.setUid(userId);
		builder.setUserToken(userToken);
		return builder.build().toByteString();
	}

	/**
	 * 获取修改性别的请求数据
	 * 
	 * @param userId
	 * @param userToken
	 * @param userSex
	 *            0=女，1=男
	 * @return
	 */
	private ByteString getModifyUserSexRequestData(int userId,
			String userToken, int userSex)
	{
		ReqSetUserInfo.Builder builder = ReqSetUserInfo.newBuilder();
		builder.setUid(userId);
		builder.setUserToken(userToken);
		builder.setUserSex(userSex);
		return builder.build().toByteString();
	}

	/**
	 * 解析修改用户信息的结果数据
	 * 
	 * @param rspPacket
	 * @return
	 * @throws Exception
	 */
	private void parseModifyUserInfoResultData(ByteString result)
	{
		try
		{
			RspSetUserInfo rspSetUserInfo = RspSetUserInfo.parseFrom(result);
			// 0=成功,1=响应错误,2=token错误,3=用户不存在或被禁用
			if (rspSetUserInfo.getRescode() == 0)
			{
				LoginedUserInfo userInfo = ToolsUtil
						.getLoginedUserInfo(rspSetUserInfo);
				// 将登录的账户信息保存到缓存中去
				ToolsUtil.saveCachDataToFile(PersonalCenterActivity.this,
						Constants.LOGINED_USER_INFO_CANCHE_FILE_NAME, userInfo);
				// 将登录后的用户信息保存到登录帐号信息管理类中去
				LoginUserInfoManager.getInstance().setLoginedUserInfo(userInfo);
				Toast.makeText(
						PersonalCenterActivity.this,
						getResources().getString(
								R.string.modify_user_info_success),
						Toast.LENGTH_SHORT).show();
			} else if (rspSetUserInfo.getRescode() == 2)
			{
				// userToken错误
				// 退出
				LoginUserInfoManager.getInstance().exitLogin();
				Toast.makeText(PersonalCenterActivity.this,
						getResources().getString(R.string.re_login),
						Toast.LENGTH_SHORT).show();
				LoginActivity.startLoginActivity(this, action);
				finish();
			} else
			{
				Toast.makeText(PersonalCenterActivity.this,
						rspSetUserInfo.getResmsg(), Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e)
		{
			DLog.e(TAG, "parseModifyUserInfoResultData()#Excepton:", e);
		}

	}

	@Override
	protected void onDestroy()
	{
		if (receiver != null)
		{
			unregisterReceiver(receiver);
		}
		super.onDestroy();
	};

	/**
	 * 获取修改昵称的请求数据
	 * 
	 * @param userId
	 * @param userToken
	 * @param userSex
	 *            0=女，1=男
	 * @return
	 */
	private ByteString getBindWeiXinRequestData(int userId, String userToken,
			String wxUnionId)
	{
		ReqSetUserInfo.Builder builder = ReqSetUserInfo.newBuilder();
		builder.setUid(userId);
		builder.setUserToken(userToken);
		builder.setWxUnionId(wxUnionId);
		// 这里传-1是为了配合后台 传入-1时 后台就不对Sex去进行处理了
		builder.setUserSex(-1);
		return builder.build().toByteString();
	}

	/**
	 * 获取绑定微信的请求数据
	 * 
	 * @param userId
	 * @param userToken
	 * @param userSex
	 *            0=女，1=男
	 * @return
	 */
	private ByteString getModifyUserNickNameRequestData(int userId,
			String userToken, String nickName)
	{
		ReqSetUserInfo.Builder builder = ReqSetUserInfo.newBuilder();
		builder.setUid(userId);
		builder.setUserToken(userToken);
		builder.setNickName(nickName);
		// 这里传-1是为了配合后台 传入-1时 后台就不对Sex去进行处理了
		builder.setUserSex(-1);
		return builder.build().toByteString();
	}

	/**
	 * 获取修改头像地址的请求数据
	 * 
	 * @param userId
	 * @param userToken
	 * @param userSex
	 *            0=女，1=男
	 * @return
	 */
	private ByteString getModifyUserPicRequestData(int userId,
			String userToken, String iconUrl)
	{
		ReqSetUserInfo.Builder builder = ReqSetUserInfo.newBuilder();
		builder.setUid(userId);
		builder.setUserToken(userToken);
		builder.setHeadPicUrl(iconUrl);
		// 这里传-1是为了配合后台 传入-1时 后台就不对Sex去进行处理了
		builder.setUserSex(-1);
		return builder.build().toByteString();
	}

	/**
	 * 跳转到本地相册界面
	 */
	private void intentLocalPhotoPicActivity()
	{
		try
		{
			final Intent intent = getPhotoPicIntent();
			startActivityForResult(intent, GET_LOCAL_PHOTO_PIC);
		} catch (ActivityNotFoundException e)
		{
			Toast.makeText(this,
					getResources().getString(R.string.get_local_photos_failed),
					Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * 获取请求Gallery的intent
	 * 
	 * @return
	 */
	private Intent getPhotoPicIntent()
	{
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
		intent.setType("image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 10);
		intent.putExtra("aspectY", 10);
		intent.putExtra("outputX", 200);
		intent.putExtra("outputY", 200);
		// intent.putExtra("return-data", true);
		intent.putExtra("return-data", false);
		return intent;
	}

	/**
	 * 跳转到拍照界面
	 */
	private void intentCameraActivity()
	{
		try
		{
			Intent intent = new Intent();
			intent.putExtra("return-data", true);
			intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT,
					Uri.fromFile(new File(CAMERA_PIC_PATH)));
			startActivityForResult(intent, CAMERA_WITH_DATA);
		} catch (Exception e)
		{
			Toast.makeText(this,
					getResources().getString(R.string.get_camera_failed),
					Toast.LENGTH_SHORT).show();
			DLog.e(TAG, "intentCameraActivity()#exception:", e);
		}
	}

	/**
	 * 裁剪图片
	 * 
	 * @param data
	 */
	private void cropPhotoPic(Intent data)
	{
		Uri currImageURI = null;
		if (data != null)
		{
			if (data.getExtras() != null)
			{
				File file = getFile((Bitmap) data.getExtras().get("data"));
				DLog.i("lilijun", "相机图片的保存地址----->>>" + file.getAbsolutePath());
				// File mCurrentPhotoFile = new File(PHOTO_DIR,
				// getPhotoFileName());//
				// 给新照的照片文件命名
				currImageURI = Uri.fromFile(file);
			} else
			{
				currImageURI = data.getData();
			}
		} else
		{
			currImageURI = Uri.fromFile(new File(CAMERA_PIC_PATH));
		}

		try
		{
			// 启动gallery去剪辑这个照片
			final Intent intent = getCropImageIntent(currImageURI);

			// startActivityForResult(intent, GET_LOCAL_PHOTO_PIC);
			startActivityForResult(intent, CROP_CAMER_PCI);
		} catch (Exception e)
		{
			Toast.makeText(this, "获取照片错误", Toast.LENGTH_LONG).show();
		}
	}

	private File getFile(Bitmap bitmap)
	{

		String sdStatus = Environment.getExternalStorageState();
		if (!sdStatus.equals(Environment.MEDIA_MOUNTED))
		{ // 检测sd是否可用

			return null;
		}
		String name = new DateFormat().format("yyyyMMdd_hhmmss",
				Calendar.getInstance(Locale.CHINA))
				+ ".jpg";

		FileOutputStream b = null;

		if (!PHOTO_DIR.isDirectory())
		{
			PHOTO_DIR.mkdirs();// 创建文件夹
		}
		File fileName = new File(PHOTO_DIR, name);

		try
		{
			b = new FileOutputStream(fileName);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, b);// 把数据写入文件
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				b.flush();
				b.close();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}

		return fileName;
	}

	/**
	 * Constructs an intent for image cropping. 调用图片剪辑程序
	 */
	private static Intent getCropImageIntent(Uri photoUri)
	{
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(photoUri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 200);
		intent.putExtra("outputY", 200);
		intent.putExtra("return-data", true);
		return intent;
	}

	/**
	 * 
	 * @param context
	 * @param uri
	 * @param REQUE_CODE_CROP
	 */
	private void startPhotoZoom(Context context, Uri uri, int REQUE_CODE_CROP)
	{
		int dp = 200;

		DLog.i("lilijun", "开始裁剪！！！@！@");
		// Intent intent = new Intent("com.android.camera.action.CROP");
		// intent.setDataAndType(uri, "image/*");
		// // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
		// intent.putExtra("crop", "true");
		// intent.putExtra("scale", true);// 去黑边
		// intent.putExtra("scaleUpIfNeeded", true);// 去黑边
		// // aspectX aspectY 是宽高的比例
		// intent.putExtra("aspectX", 1);// 输出是X方向的比例
		// intent.putExtra("aspectY", 1);
		// // outputX outputY 是裁剪图片宽高，切忌不要再改动下列数字，会卡死
		// intent.putExtra("outputX", dp);// 输出X方向的像素
		// intent.putExtra("outputY", dp);
		// intent.putExtra("outputFormat",
		// Bitmap.CompressFormat.PNG.toString());
		// intent.putExtra("noFaceDetection", true);
		// intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		// intent.putExtra("return-data", false);// 设置为不返回数据

		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 200);
		intent.putExtra("outputY", 200);
		// intent.putExtra("outputFormat",
		// Bitmap.CompressFormat.PNG.toString());
		intent.putExtra("return-data", false);

		startActivityForResult(intent, REQUE_CODE_CROP);
	}

	private BroadcastReceiver receiver = new BroadcastReceiver()
	{
		public void onReceive(Context context, Intent intent)
		{
			if (intent.getAction().equals(Constants.ACTION_ACCOUNT_HAVE_MODIFY))
			{
				if (!LoginUserInfoManager.getInstance().isHaveUserLogin())
				{
					// 接收到用户需要重新登录的广播后 直接finish掉此界面(在用户更换了手机号之后需要重新登录)
					finish();
				}
			}
		};
	};

	public static void startPersonalActivity(Context context, String action)
	{
		DataCollectionManager.startActivity(context, new Intent(context,
				PersonalCenterActivity.class), action);
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		loadingDialog.dismiss();
		setData();
	}

	@Override
	public void onSuccess()
	{
		if (PersonalCenterActivity.this != null)
		{
			runOnUiThread(new Runnable()
			{

				@Override
				public void run()
				{
					loadingDialog.dismiss();
					setData();
					// 发送用户的相关信息发生了改变的广播
					sendBroadcast(new Intent(
							Constants.ACTION_LOGINED_USER_INFO_HAVE_MODIFY));
				}
			});
		}
	}

	@Override
	public void onError(final String msg)
	{
		if (PersonalCenterActivity.this != null)
		{
			runOnUiThread(new Runnable()
			{

				@Override
				public void run()
				{
					loadingDialog.dismiss();
					if ("-1".equals(msg))
					{
						// userToken错误
						// 退出登录
						LoginUserInfoManager.getInstance().exitLogin();
						Toast.makeText(PersonalCenterActivity.this,
								getResources().getString(R.string.re_login),
								Toast.LENGTH_SHORT).show();
						LoginActivity.startLoginActivity(
								PersonalCenterActivity.this, action);
						finish();
					} else
					{
						Toast.makeText(PersonalCenterActivity.this, msg,
								Toast.LENGTH_SHORT).show();
					}
				}

			});
		}
	}

	@Override
	public void onSubmitData()
	{
		if (PersonalCenterActivity.this != null)
		{
			runOnUiThread(new Runnable()
			{

				@Override
				public void run()
				{
					loadingDialog.show();
				}
			});
		}
	}

}
