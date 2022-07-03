//
//  NEVideoOperationView.h
//  NLiteAVDemo
//
//  Created by I am Groot on 2020/8/25.
//  Copyright © 2020 Netease. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface NEVideoOperationView : UIView
@property(strong,nonatomic)UIButton *microPhone;
@property(strong,nonatomic)UIButton *cameraBtn;
@property(strong,nonatomic)UIButton *hangupBtn;
@property(strong,nonatomic)UIButton *speakerBtn;
@property(strong,nonatomic)UIButton *mediaBtn;

- (void)changeAudioStyle;
- (void)changeVideoStyle;

@end

NS_ASSUME_NONNULL_END
