
#!bin/bash
#sleep 10
if [ -f ~/.bash_profile ];
then
. ~/.bash_profile
fi

#读取上次的pid号
pidLast=`cat ./info.txt`
#echo pidLast:$pidLast 
#把pid保存起来
ps -ef|grep phantomjs |awk '{print $2}'>info2.txt
ps -ef|grep phantomjs |awk '{print $2}'>info.txt
#获得当前的进程号
pidCur=`cat ./info2.txt`
#echo pdiCur:$pidCur
pidLast=${pidLast// /}
#判断当前的进程号是否包含上次的进程号
for element in $pidLast
do
 #echo $element
#包含的话就结束进程
#[[ $pidCur =~ $element ]] && echo "\$pidCur contains $element"
[[ $pidCur =~ $element ]] && kill -9 $element && echo $element phantomjs进程被系统成功结束
#不包含继续跑
#[[ $pidCur =~ $element ]] || echo "\$pidCur NOT contains $element"
done 




