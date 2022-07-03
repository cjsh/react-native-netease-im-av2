//
//  OpenMeeting.m
//  app
//
//  Created by 雨浓科技 on 2019/12/11.
//  Copyright © 2019年 Facebook. All rights reserved.
//

#import "OpenMeeting.h"
#import <React/RCTUtils.h>
#import <React/RCTBridge.h>
#import <React/RCTEventDispatcher.h>
#import "NTESTeamMeetingViewController.h"
#import "NTESTeamMeetingCallerInfo.h"

@implementation OpenMeeting

RCT_EXPORT_MODULE();

//me:自己的账号；members：群成员；teamId：群ID
RCT_EXPORT_METHOD (presentMeeting:(nonnull NSString *)me:(nonnull NSArray *)members: (nonnull NSString *)teamId)
{
  NTESTeamMeetingCallerInfo *info = [[NTESTeamMeetingCallerInfo alloc] init];
  info.members = [@[me] arrayByAddingObjectsFromArray:members];
  info.teamId = teamId;
  info.teamType = NIMKitTeamTypeNomal;
  NTESTeamMeetingViewController *vc = [[NTESTeamMeetingViewController alloc] initWithCallerInfo:info];
  UIViewController *rootViewController = RCTPresentedViewController();
  [rootViewController presentViewController:vc animated:NO completion:nil];
}
@end

