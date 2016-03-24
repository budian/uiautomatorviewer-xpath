# uiautomatorviewer-xpath
重写android sdk自带的uiautomatorviewer工具，添加xpath

添加字段说明： 
   xpath：如果isOnly=true，则说明该xpath值在当前页面是唯一的，否则取得是从根节点开始的xpath:
   fXpath:从根节点开始的xpath,去掉了index；
   uiaSelector：上下滑动查找用的；
   xpath2：同fXpath，保留了index；
   
如果页面有滑动的话，从根节点开始的xpath需要慎用，因为该工具是根据dump当前页面的元素去生成的xml，是会变的，最好不要用含有index的xpath   
