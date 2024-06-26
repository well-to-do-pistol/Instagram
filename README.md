Instagram:

零:

> 1.等待动画用ProgressDialog
>
> 2.应用栏一律用appbar加toolbar(有左箭头回退)
>
> 3.头像等圆框图一律用hdodenhof/circleimageview(替换ImageView)
>
> 4.显示图片一律用Picasso库(直接根据链接进入数据库)

1.  登录和注册(Firebase邮箱密码):

```{=html}

```

1.  不用互传数据, 直接用currentUser

2.  注册在FirebaseAuth保存,
    在RealtimeDB用HashMap保存Users文档(ID是子文档名)

```{=html}

```

2.  主页面

```{=html}

```

1.  底部导航栏用GoogleMaterial的bottomnavigation中放menu

2.  根据id变换相应fragment

```{=html}

```

3.  PostActivity(上传页面)

```{=html}

```

1.  使用android-image-cropper库从文件(相机)拿到图片并剪切修改

2.  用hendraanggrian/socialview库实现标签功能

3.  用FirebaseStorage保存图片(Posts),
    并在RealtimeDB保存Posts文档(随机ID是子文档名)(记录了图片链接)

4.  用getHashtags()提取文字中的标签, 在RealtimeD保存HashTags

5.  往检索栏加入HashtagArrayAdapter(检索数据库得到的Hashtag)

```{=html}

```

4.  SearchFragment(检索页面)(用户和标签)

```{=html}

```

1.  检索输入栏用SocialAutoCompleteTextView

2.  关注与被关注在在RealtimeD保存Follow(用户ID是子文档名,
    孙文档名是follower或following)

3.  (一开始显示全部)用Query检索username获得对象加进列表(传给adapter),
    通知; 遍历标签数组, 如果名字包含, 则加进检索数组, 显示(名字和数量)

```{=html}

```

5.  HomeFragment

```{=html}

```

1.  LinearLayoutManager管理PostItem从尾开始显示

2.  喜欢按钮, 保存在RealtimeDB的Likes文档的postId下userId设置为true

3.  在RealtimeDB保存Saves(收藏)文档(userId子文档(postId子文档))

```{=html}

```

6.  CommentActivity

```{=html}

```

1.  在RealtimeDB保存Comments文档(postId子文档)

2.  点击评论可跳转该人Profile

3.  设置长按利用alertDialog询问删除

```{=html}

```

7.  ProfileFragment(xml最复杂)(个人信息页面)

```{=html}

```

1.  用getSharedPreferences(PROFILE)接收数据(profileId), 没有直接拿当前.
    只要有头像或其他相关点击就要显示

2.  检索Saves(收藏)文档(userId子文档(postId子文档))拿到收藏列表,
    再拿收藏列表和检索Posts得到的Id对照, 点击收藏键显示收藏的Post

3.  用rengwuxian/MaterialEditText库作输入框

4.  点击更改头像时, 用FirebaseStorage保存图片(Uploads),
    用android-image-cropper拿到uri再用StorageTask更新uri到Users(数据库一改变,
    显示的图像自然也改变)

```{=html}

```

8.  ProfileDetailFragment(明细页面)

```{=html}

```

1.  用getSharedPreferences(PREFS)接收数据(profileId),
    只要有Post图片点击就要显示

```{=html}

```

9.  NotificationFragment(通知页面)(根据isPost的是否改变显示(是显示Post,
    否显示Profile))(注意get和set函数命名正确(isIsPost)否则永远拿到false)

> 1.通知holder塞个PREFS或PROFILE,
> 点击出Post(isPost是)(表示评论或喜欢或收藏通知),
> 出Profile(isPost否)(表示关注通知)
>
> 2.检索RealtimeDB的Notifications文档中的userId显示通知

十. FollowerActivity(关注或被关注或喜欢列表)

1.往里放userAdapter(根据三种中的一种筛选id)

2.点击Profile中followers中文字时打开\
3.点击postAdapter中noOfLikes打开

十一.OptionActivity(用于Logout返回登录)
