package com.xd.leplay.store.gui.game;

import android.content.Context;
import android.content.Intent;

import com.xd.leplay.store.control.DataCollectionManager;
import com.xd.leplay.store.gui.main.BaseActivity;

/**
 * 礼包详情界面2(直接从网络获取礼包详情)
 * 
 * @author lilijun
 *
 */
public class GiftDetailActivity2 extends BaseActivity
{
	// TODO 暂时没有这个功能，注释掉 不要删掉
	// private static final String TAG = "GiftDetailActivity2";
	//
	// private DetailGiftInfo detailGiftInfo = null;
	//
	// private ListAppInfo appInfo = null;
	//
	// private Map<Integer, GiftInfo> giftInfoMap = null;
	//
	// private long gameId = -1;
	//
	// private int showGiftId = -1;
	//
	// private LinearLayout appLay;
	// private ImageView appIcon;
	// private TextView appName, appDownloadAndSize;
	// private DownloadProgressButton appDownloadStateBtn;
	// private LinearLayout giftNumLay;
	// private TextView giftNum1;
	// private TextView[] giftNums;
	// private TextView giftName, giftValieDate, giftContent, giftEligibility,
	// getGiftWay, giftCode;
	// private LinearLayout showCodeLay;
	// private Button getCodeBtn;
	//
	// private DownloadOnclickListener downloadOnclickListener;
	//
	// private ImageLoaderManager imageLoaderManager = null;
	//
	// private DisplayImageOptions options = null;
	//
	// /** 抢礼包的请求TAG */
	// private final String GET_GIFT_REQUEST_TAG = "ReqPickupGameBag";
	//
	// /** 抢礼包的响应TAG */
	// private final String GET_GIFT_RSPONSE_TAG = "RspPickupGameBag";
	//
	// /** 获取礼包详情的请求TAG */
	// private final String GET_GIFT_DETAIL_REQUEST_TAG = "ReqGameBag";
	//
	// /** 获取礼包详情的响应TAG */
	// private final String GET_GIFT_DETAIL_RSPONSE_TAG = "RspGameBag";
	//
	// /** 正在获取礼包的Dialog */
	// private LoadingDialog loadingDialog = null;
	//
	// /** 从网路单独获取礼包详情的etag标识值 */
	// private String etagMark = "sigleGiftDetail";
	//
	@Override
	protected void initView()
	{
		// action = DataCollectionManager.getAction(DataCollectionManager
		// .getIntentDataCollectionAction(getIntent()),
		// DataCollectionConstant.DATA_COLLECTION_GIFT_DETAIL_VALUE);
		// DataCollectionManager.getInstance().addRecord(action);
		//
		// titleView.setTitleName(getResources().getString(R.string.gift_detail));
		// titleView.setRightLayVisible(false);
		// titleView.setBottomLineVisible(true);
		//
		// centerViewLayout.setVisibility(View.GONE);
		//
		// imageLoaderManager = ImageLoaderManager.getInstance();
		// options = DisplayUtil.getListIconImageLoaderOptions();
		//
		// gameId = getIntent().getLongExtra("game_id", -1);
		//
		// showGiftId = getIntent().getIntExtra("show_gift_id", -1);
		//
		// loadingDialog = new LoadingDialog(this, getResources().getString(
		// R.string.getting_gift));
		//
		// setCenterView(R.layout.gift_detail_activity);
		//
		// appLay = (LinearLayout) findViewById(R.id.gift_detail_app_lay);
		// appLay.setOnClickListener(new OnClickListener()
		// {
		// @Override
		// public void onClick(View v)
		// {
		// DetailsActivity.startDetailsActivityById(
		// GiftDetailActivity2.this, appInfo.getSoftId(), action);
		// }
		// });
		// appIcon = (ImageView) findViewById(R.id.gift_detail_app_icon);
		// appName = (TextView) findViewById(R.id.gift_detail_app_name);
		// appDownloadAndSize = (TextView)
		// findViewById(R.id.gift_detail_download_count_and_size);
		// appDownloadStateBtn = (DownloadProgressButton)
		// findViewById(R.id.gift_detail_download_btn);
		// giftNumLay = (LinearLayout)
		// findViewById(R.id.gift_detail_gifts_num_title_lay);
		// giftNum1 = (TextView) findViewById(R.id.gift_detail_num_title1);
		// giftName = (TextView) findViewById(R.id.gift_detail_gift_name);
		// giftValieDate = (TextView)
		// findViewById(R.id.gift_detail_gift_vaile_date);
		// giftContent = (TextView)
		// findViewById(R.id.gift_detail_gift_content_describle);
		// giftEligibility = (TextView)
		// findViewById(R.id.gift_detail_get_gift_eligibility_describle);
		// getGiftWay = (TextView)
		// findViewById(R.id.gift_detail_get_gift_way_describle);
		// giftCode = (TextView) findViewById(R.id.gift_detail_redeem_code);
		// showCodeLay = (LinearLayout)
		// findViewById(R.id.gift_detail_show_code_lay);
		// getCodeBtn = (Button)
		// findViewById(R.id.gift_detail_get_redeem_code_btn);
		//
		// detailGiftInfo = new DetailGiftInfo();
		// // 获取礼包详情数据
		// doLoadData(Constants.APP_API_URL, new String[]
		// { GET_GIFT_DETAIL_REQUEST_TAG }, new ByteString[]
		// { getGiftDetailRequestData(gameId) }, etagMark);
	}

	// private void initData()
	// {
	// appInfo = detailGiftInfo.getListAppInfo();
	// giftInfoMap = detailGiftInfo.getGiftInfoMap();
	// imageLoaderManager.displayImage(appInfo.getIconUrl(), appIcon, options);
	// appName.setText(appInfo.getName());
	// appDownloadAndSize.setText(appInfo.getFormatDownloadCount()
	// + getResources().getString(R.string.count_download) + "|"
	// + appInfo.getFormatSize());
	// downloadOnclickListener = new DownloadOnclickListener(this, action);
	// downloadOnclickListener.setDownloadListenerInfo(appInfo);
	// appDownloadStateBtn.downloadbtn
	// .setOnClickListener(downloadOnclickListener);
	// appDownloadStateBtn.setInfo(appInfo.getPackageName());
	//
	// if (giftInfoMap != null)
	// {
	// if (giftInfoMap.size() > 1)
	// {
	// giftNumLay.setVisibility(View.VISIBLE);
	// giftNums = new TextView[giftInfoMap.size()];
	// LayoutParams params = (LayoutParams) giftNum1.getLayoutParams();
	// // float textSize = giftNum1.getTextSize();
	// giftNumLay.removeAllViews();
	// int count = 0;
	// for (Entry<Integer, GiftInfo> entry : giftInfoMap.entrySet())
	// {
	// giftNums[count] = new TextView(this);
	// giftNums[count].setLayoutParams(params);
	// giftNums[count].setGravity(Gravity.CENTER);
	// // giftNums[count].setTextSize(textSize);
	// giftNums[count].setText((count + 1) + "");
	// if (count == 0 && showGiftId == -1)
	// {
	// showGiftId = entry.getValue().getId();
	// }
	// if (entry.getValue().getId() == showGiftId)
	// {
	// giftNums[count]
	// .setBackgroundResource(R.drawable.circle_blue_bg_shape);
	// giftNums[count].setTextColor(getResources().getColor(
	// R.color.white));
	// setGiftInfoData(entry.getValue());
	// } else
	// {
	// giftNums[count]
	// .setBackgroundResource(R.drawable.circle_gray_bg_shape);
	// giftNums[count].setTextColor(getResources().getColor(
	// R.color.list_soft_describe_color));
	// }
	// giftNums[count]
	// .setOnClickListener(giftTitleOnClickListener);
	// giftNums[count].setTag(entry.getValue());
	// giftNums[count].setTag(R.id.gift_detail_gift_name, count);
	// giftNumLay.addView(giftNums[count]);
	// count++;
	// }
	//
	// } else
	// {
	// giftNumLay.setVisibility(View.GONE);
	// // giftNum1.setText(1 + "");
	// // giftNum1.setBackgroundResource(R.drawable.circle_blue_bg_shape);
	// // giftNum1.setTextColor(getResources().getColor(R.color.white));
	// setGiftInfoData(giftInfoMap.get(showGiftId));
	// }
	// }
	// loadingView.setVisibilyView(false);
	// centerViewLayout.setVisibility(View.VISIBLE);
	// }
	//
	// @Override
	// protected void loadDataSuccess(RspPacket rspPacket)
	// {
	// super.loadDataSuccess(rspPacket);
	// if (rspPacket.getAction(0).equals(GET_GIFT_DETAIL_RSPONSE_TAG))
	// {
	// // 获取礼包详情数据返回
	// try
	// {
	// RspGameBag rspGameBag = RspGameBag.parseFrom(rspPacket
	// .getParams(0));
	// if (rspGameBag.getRescode() == 0)
	// {
	// // 游戏礼包列表数据返回
	// parseGameGiftDetailResult(rspPacket);
	// initData();
	// }
	// } catch (Exception e)
	// {
	// DLog.e(TAG, "获取礼包详情发生异常#Excepton:", e);
	// loadingView.setVisibilyView(false);
	// centerViewLayout.setVisibility(View.GONE);
	// errorViewLayout.setVisibility(View.VISIBLE);
	// }
	// } else if (rspPacket.getAction(0).equals(GET_GIFT_RSPONSE_TAG))
	// {
	// // 抢礼包数据返回
	// try
	// {
	// RspPickupGameBag rspPickupGameBag = RspPickupGameBag
	// .parseFrom(rspPacket.getParams(0));
	// if (rspPickupGameBag.getRescode() == 0)
	// {
	// String redeemCode = rspPickupGameBag.getRedeemCode();
	// // 找到之前列表获取到并存放到本地的礼包数据
	// GiftInfo giftInfo = detailGiftInfo.getGiftInfoMap().get(
	// rspPickupGameBag.getBagId());
	// giftInfo.setCode(redeemCode);
	// // 将得到的礼包信息添加到用户的我的礼包列表中去
	// LoginUserInfoManager.getInstance().getLoginedUserInfo()
	// .getGiftList().put(giftInfo.getId(), giftInfo);
	// LoginUserInfoManager.getInstance().getLoginedUserInfo()
	// .getGiftIdList().add(giftInfo.getId());
	//
	// // 将登录的账户信息保存到缓存中去
	// ToolsUtil.saveCachDataToFile(this,
	// Constants.LOGINED_USER_INFO_CANCHE_FILE_NAME,
	// LoginUserInfoManager.getInstance()
	// .getLoginedUserInfo());
	//
	// // 发送用户信息改变广播
	// sendBroadcast(new Intent(
	// Constants.ACTION_ACCOUNT_HAVE_MODIFY));
	//
	// // 显示领取礼包成功的Dialog
	// showGetGiftSuccessDialog(giftInfo);
	//
	// if (showGiftId == giftInfo.getId())
	// {
	// setGiftInfoData(giftInfo);
	// }
	// loadingDialog.dismiss();
	//
	// } else if (rspPickupGameBag.getRescode() == 3)
	// {
	// // 礼包已被抢完
	// loadingDialog.dismiss();
	// Toast.makeText(this,
	// getResources().getString(R.string.no_gift),
	// Toast.LENGTH_SHORT).show();
	// } else if (rspPickupGameBag.getRescode() == 4)
	// {
	// // 已领取过
	// loadingDialog.dismiss();
	// Toast.makeText(
	// this,
	// getResources().getString(R.string.already_get_gift),
	// Toast.LENGTH_SHORT).show();
	// } else
	// {
	// DLog.e("lilijun",
	// "领取礼包失败--失败码-->>>" + rspPickupGameBag.getRescode());
	// Toast.makeText(this, rspPickupGameBag.getResmsg(),
	// Toast.LENGTH_SHORT).show();
	// }
	// } catch (Exception e)
	// {
	// DLog.e(TAG, "抢礼包发生异常#Excepton:", e);
	// }
	// }
	// }
	//
	// @Override
	// protected void loadDataFailed(RspPacket rspPacket)
	// {
	// super.loadDataFailed(rspPacket);
	// for (String action : rspPacket.getActionList())
	// {
	// if (action.equals(GET_GIFT_DETAIL_REQUEST_TAG))
	// {
	// // 获取礼包详情数据失败
	// errorViewLayout.setVisibility(View.VISIBLE);
	// loadingView.setVisibilyView(false);
	// centerViewLayout.setVisibility(View.GONE);
	// } else if (action.equals(GET_GIFT_REQUEST_TAG))
	// {
	// // 获取礼包数据失败
	// loadingDialog.dismiss();
	// Toast.makeText(this,
	// getResources().getString(R.string.get_gift_failed),
	// Toast.LENGTH_SHORT).show();
	// }
	// }
	// }
	//
	// @Override
	// protected void netError(String[] actions)
	// {
	// super.netError(actions);
	// for (String action : actions)
	// {
	// if (action.equals(GET_GIFT_DETAIL_REQUEST_TAG))
	// {
	// // 获取礼包详情数据失败
	// errorViewLayout.setVisibility(View.VISIBLE);
	// loadingView.setVisibilyView(false);
	// centerViewLayout.setVisibility(View.GONE);
	// } else if (action.equals(GET_GIFT_REQUEST_TAG))
	// {
	// // 获取礼包数据失败
	// loadingDialog.dismiss();
	// Toast.makeText(this,
	// getResources().getString(R.string.get_gift_failed),
	// Toast.LENGTH_SHORT).show();
	// }
	// }
	// }
	//
	// @Override
	// protected void tryAgain()
	// {
	// super.tryAgain();
	//
	// // 获取礼包详情数据
	// doLoadData(Constants.APP_API_URL, new String[]
	// { GET_GIFT_DETAIL_REQUEST_TAG }, new ByteString[]
	// { getGiftDetailRequestData(gameId) }, etagMark);
	// }
	//
	// /**
	// * 礼包数字点击事件
	// */
	// private OnClickListener giftTitleOnClickListener = new OnClickListener()
	// {
	//
	// @Override
	// public void onClick(View v)
	// {
	// GiftInfo giftInfo = (GiftInfo) v.getTag();
	// showGiftId = giftInfo.getId();
	// int position = (Integer) v.getTag(R.id.gift_detail_gift_name);
	// setGiftInfoData(giftInfo);
	// for (int i = 0; i < giftNums.length; i++)
	// {
	// if (i == position)
	// {
	// giftNums[i]
	// .setBackgroundResource(R.drawable.circle_blue_bg_shape);
	// giftNums[i].setTextColor(getResources().getColor(
	// R.color.white));
	// } else
	// {
	// giftNums[i]
	// .setBackgroundResource(R.drawable.circle_gray_bg_shape);
	// giftNums[i].setTextColor(getResources().getColor(
	// R.color.list_soft_describe_color));
	// }
	// }
	// }
	// };
	//
	// /**
	// * 设置礼包信息
	// *
	// * @param giftInfo
	// */
	// private void setGiftInfoData(final GiftInfo giftInfo)
	// {
	// giftName.setText(giftInfo.getName());
	// giftValieDate
	// .setText(getResources().getString(R.string.valied_data)
	// + giftInfo.getStartTime()
	// + getResources().getString(R.string.to)
	// + giftInfo.getEndTime());
	// giftContent.setText(giftInfo.getContent());
	// giftEligibility.setText(giftInfo.getEligibility());
	// getGiftWay.setText(giftInfo.getHowPickup());
	// if (LoginUserInfoManager.getInstance().isHaveUserLogin())
	// {
	// if (LoginUserInfoManager.getInstance().getLoginedUserInfo()
	// .getGiftIdList().contains(giftInfo.getId()))
	// {
	// showCodeLay.setVisibility(View.VISIBLE);
	// giftInfo.setCode(LoginUserInfoManager.getInstance()
	// .getLoginedUserInfo().getGiftList()
	// .get(giftInfo.getId()).getCode());
	// // 显示我的礼包列表中的验证码
	// giftCode.setText(giftInfo.getCode());
	// getCodeBtn
	// .setText(getResources().getString(R.string.copy_code));
	// getCodeBtn.setOnClickListener(null);
	// getCodeBtn.setOnClickListener(new OnClickListener()
	// {
	// @Override
	// public void onClick(View v)
	// {
	// // 复制礼包兑换码
	// boolean isCopy = ToolsUtil.copy(giftInfo.getCode(),
	// GiftDetailActivity2.this);
	//
	// if (isCopy)
	// {
	// Toast.makeText(
	// GiftDetailActivity2.this,
	// getResources().getString(
	// R.string.copy_code_success),
	// Toast.LENGTH_SHORT).show();
	// } else
	// {
	// Toast.makeText(
	// GiftDetailActivity2.this,
	// GiftDetailActivity2.this.getResources()
	// .getString(R.string.copy_failed),
	// Toast.LENGTH_SHORT).show();
	// }
	// }
	// });
	// } else
	// {
	// showCodeLay.setVisibility(View.GONE);
	// getCodeBtn
	// .setText(getResources().getString(R.string.grab_gift));
	// getCodeBtn.setOnClickListener(new OnClickListener()
	// {
	// @Override
	// public void onClick(View v)
	// {
	// if (LoginUserInfoManager.getInstance()
	// .isHaveUserLogin())
	// {
	// // 获取礼包兑换码
	// loadingDialog.show();
	// doLoadData(
	// Constants.UAC_API_URL,
	// new String[]
	// { GET_GIFT_REQUEST_TAG },
	// new ByteString[]
	// { getPickupGiftRequestData(
	// appInfo.getSoftId(),
	// giftInfo.getId()) }, "");
	// } else
	// {
	// LoginActivity.startLoginActivity(
	// GiftDetailActivity2.this, action);
	// }
	// }
	// });
	// }
	// } else
	// {
	// showCodeLay.setVisibility(View.GONE);
	// getCodeBtn.setText(getResources().getString(R.string.grab_gift));
	// getCodeBtn.setOnClickListener(new OnClickListener()
	// {
	// @Override
	// public void onClick(View v)
	// {
	// if (LoginUserInfoManager.getInstance().isHaveUserLogin())
	// {
	// // 获取礼包兑换码
	// loadingDialog.show();
	// doLoadData(
	// Constants.UAC_API_URL,
	// new String[]
	// { GET_GIFT_REQUEST_TAG },
	// new ByteString[]
	// { getPickupGiftRequestData(appInfo.getSoftId(),
	// giftInfo.getId()) }, "");
	// } else
	// {
	// LoginActivity.startLoginActivity(
	// GiftDetailActivity2.this, action);
	// }
	// }
	// });
	// }
	// }
	//
	// /**
	// * 获取礼包详情的请求数据
	// *
	// * @param gameId
	// * @return
	// */
	// private ByteString getGiftDetailRequestData(long gameId)
	// {
	// ReqGameBag.Builder builder = ReqGameBag.newBuilder();
	// builder.setPageIndex(1);
	// builder.setPageSize(1);
	// builder.setGameId(gameId);
	// return builder.build().toByteString();
	// }
	//
	// /**
	// * 解析礼包详情返回结果
	// *
	// * @param rspPacket
	// * @throws Exception
	// */
	// private void parseGameGiftDetailResult(RspPacket rspPacket)
	// {
	// try
	// {
	// RspGameBag rspGameBag = RspGameBag
	// .parseFrom(rspPacket.getParams(0));
	//
	// // 返回码：0=成功,1=失败,2=暂无数据
	// if (rspGameBag.getRescode() == 0)
	// {
	// // 成功
	// List<Game> infos = rspGameBag.getGameList();
	//
	// if (infos == null || infos.isEmpty())
	// {
	// // 没有礼包详情数据返回
	// // isHaveNextPageData = false;
	// DLog.i("lilijun", "没有详情数据返回！！@！@！");
	// }
	// if (infos != null)
	// {
	// for (Game game : infos)
	// {
	// ListAppInfo listAppInfo = new ListAppInfo();
	// listAppInfo.setSoftId(game.getGameId());
	// listAppInfo.setName(game.getGameName());
	// listAppInfo.setDownloadCount(game.getDownTimes());
	// listAppInfo.setFormatDownloadCount(ToolsUtil
	// .getFormatDownloadCount(listAppInfo
	// .getDownloadCount()));
	// listAppInfo.setSize(game.getPackSize());
	// listAppInfo.setFormatSize(ToolsUtil
	// .getFormatSize(listAppInfo.getSize()));
	// listAppInfo.setPackageName(game.getPackName());
	// listAppInfo.setDownlaodUrl(game.getPackUrl());
	// listAppInfo.setIconUrl(game.getGameIconUrl());
	//
	// // 添加数据到礼包详情数据集合中去
	// detailGiftInfo.setListAppInfo(listAppInfo);
	// for (App.GameBag gift : game.getGameBagList())
	// {
	// GiftInfo giftInfo = new GiftInfo();
	// giftInfo.setId(gift.getBagId());
	// giftInfo.setName(gift.getBagName());
	// giftInfo.setContent(gift.getBagContent());
	// giftInfo.setEligibility(gift.getEligibility());
	// giftInfo.setEndTime(gift.getEndTime());
	// giftInfo.setHowPickup(gift.getHowPickup());
	// giftInfo.setStartTime(gift.getStartTime());
	// giftInfo.setUsage(gift.getUsage());
	// giftInfo.setGameIconUrl(listAppInfo.getIconUrl());
	// giftInfo.setGameId(listAppInfo.getSoftId());
	// giftInfo.setGameName(listAppInfo.getName());
	// detailGiftInfo.getGiftInfoMap().put(
	// giftInfo.getId(), giftInfo);
	// }
	// }
	// }
	// }
	// } catch (Exception e)
	// {
	// DLog.e(TAG, "parseGameGiftDetailResult()#Excepton:", e);
	// }
	// }
	//
	// /**
	// * 获取领取礼包的请求数据
	// *
	// * @param gift
	// * @return
	// */
	// private ByteString getPickupGiftRequestData(long gameId, int giftId)
	// {
	// ListAppInfo appInfo = detailGiftInfo.getListAppInfo();
	// GiftInfo gift = detailGiftInfo.getGiftInfoMap().get(giftId);
	//
	// LoginedUserInfo loginedUserInfo = LoginUserInfoManager.getInstance()
	// .getLoginedUserInfo();
	// ReqPickupGameBag.Builder builder = ReqPickupGameBag.newBuilder();
	// builder.setUid(loginedUserInfo.getUserId());
	// builder.setUserToken(loginedUserInfo.getUserToken());
	// builder.setGameId(appInfo.getSoftId());
	// builder.setGameName(appInfo.getName());
	// builder.setGameIconUrl(appInfo.getIconUrl());
	// builder.setBagId(gift.getId());
	// builder.setBagName(gift.getName());
	// builder.setStartTime(gift.getStartTime());
	// builder.setEndTime(gift.getEndTime());
	// return builder.build().toByteString();
	// }
	//
	// /**
	// * 礼包领取成功的Dialog
	// *
	// * @param giftInfo
	// */
	// private void showGetGiftSuccessDialog(final GiftInfo giftInfo)
	// {
	// final CenterDialog getGiftSuccessDialog = new CenterDialog(this);
	// getGiftSuccessDialog.show();
	// getGiftSuccessDialog.setTitleName(getResources().getString(
	// R.string.get_gift_success));
	// View giftDialogView = View.inflate(this,
	// R.layout.get_gift_success_dialog, null);
	// TextView useWay = (TextView) giftDialogView
	// .findViewById(R.id.gift_dialog_use_way);
	// TextView redeemCode = (TextView) giftDialogView
	// .findViewById(R.id.gift_dialog_redeem_code);
	// Button copyCodeBtn = (Button) giftDialogView
	// .findViewById(R.id.gift_dialog_copy_redeem_code);
	// String useWayStr = getResources().getString(R.string.use_way)
	// + giftInfo.getUsage();
	// useWay.setText(ToolsUtil.getFormatTextColor(useWayStr, 0, 5, "#666666"));
	// redeemCode.setText(giftInfo.getCode());
	// copyCodeBtn.setOnClickListener(new OnClickListener()
	// {
	// @Override
	// public void onClick(View v)
	// {
	// boolean isCopy = ToolsUtil.copy(giftInfo.getCode(),
	// GiftDetailActivity2.this);
	// if (isCopy)
	// {
	// Toast.makeText(
	// GiftDetailActivity2.this,
	// getResources()
	// .getString(R.string.copy_code_success),
	// Toast.LENGTH_SHORT).show();
	// } else
	// {
	// Toast.makeText(
	// GiftDetailActivity2.this,
	// GiftDetailActivity2.this.getResources().getString(
	// R.string.copy_failed), Toast.LENGTH_SHORT)
	// .show();
	// }
	// getGiftSuccessDialog.dismiss();
	// }
	// });
	// getGiftSuccessDialog.setCenterView(giftDialogView);
	// }
	//
	public static void startGiftDetailActivity2(Context context, long gameId,
			int showGiftId, String action)
	{
		Intent intent = new Intent(context, GiftDetailActivity2.class);
		intent.putExtra("game_id", gameId);
		intent.putExtra("show_gift_id", showGiftId);
		DataCollectionManager.startActivity(context, intent, action);
	}
	//
	// public static void startGiftDetailActivity2(Context context, long gameId,
	// String action)
	// {
	// Intent intent = new Intent(context, GiftDetailActivity2.class);
	// intent.putExtra("game_id", gameId);
	// intent.putExtra("show_gift_id", -1);
	// DataCollectionManager.startActivity(context, intent, action);
	// }

}
