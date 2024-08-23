## 一.所有数据库表

1. #### Comments

   - postId
     - userId
       - commentId
         - comment
         - id
         - publisher

2. #### Follow

   - userId
     - following
       - userId
         - boolean
     - followers
       - userId
         - boolean

3. #### HashTags

   - name
     - postId
       - postid
       - tag(name)

4. #### Likes

   - ​	postId
     - userId
       - boolean

5. #### Notifications

   - userId
     - postId
       - notificationId
         - isPost(boolean)
         - postid
         - text
         - userid

6. #### Posts

   - ​	postId
     - description
     - imageurl
     - postid
     - publisher

7. #### Saves

   - ​	userId
     - postId(true)

8. #### Users

   - ​	userId
     - bio
     - email
     - fullname
     - id
     - imageurl
     - name
     - username

ID是怎么来的?

userId:   `getCurrentUser().getUid()`

storage里图片id:   `System.currentTimeMills()+"."+文件扩展名`

postId, commentId:   `ref.push().getKey()`

## 二.使用标签库, 截图库, 圆图库, Picasso, 扔物线库

1. #### github.com/hendraanggrian/socialview

   - 帖子里内容的编辑栏改为SocialAutoCompleteTextView, 配置特定的xml格式
   - view.getHashtags()得到标签(识别带# , 文字会变色)
   - 主页帖子里添加和搜索框的编辑栏改为SocialAutoCompleteTextView
   - 在PostActivity里onStart检索数据库拿到标签名字和使用数, 构建Hashtag, 添加到HashtagArrayAdapter, 再添加到帖子里内容的编辑栏view(编辑时#加任意字符可弹出相应的标签选择)

2. #### github.com/ArthurHub/Android-Image-Cropper

   - CropImage.activity启动后重写onActivityResult获得图片Uri(无需加其他语句, 打开activity就启动图库或文件管理来获取图片)
   - 要加特定activity(manifest)

3. #### github.com/hdodenhof/CircleImageView

   - CircleImageView替代ImageView(不用任何设置即可变圆图)

4. #### github.com/square/picasso

   - 用url取图片都用毕加索

5. #### github.com/rengwuxian/MaterialEditText

   - 丰富编辑栏ui(MaterialEditText)
