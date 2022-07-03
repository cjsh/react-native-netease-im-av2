//
//  NTESTeamMeetingCallerInfo.h
//  NIM
//
//  Created by chris on 2017/5/5.
//  Copyright © 2017年 Netease. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "NIMKitInfo.h"
#import "NIMKitTeamType.h"

@interface NTESTeamMeetingCallerInfo : NSObject

@property (nonatomic,copy) NSArray *members;

@property (nonatomic,copy) NSString *teamId;

@property (nonatomic,assign) NIMKitTeamType teamType;

@end
