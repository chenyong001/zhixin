
CREATE DATABASE m2c DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci;
#创建账户
create user 'm2c'@'%' identified by  'M2c@123456';
#赋予权限，with grant option这个选项表示该用户可以将自己拥有的权限授权给别人
grant all privileges on m2c.* to 'm2c'@'%' with grant option;
#改密码&授权超用户，flush privileges 命令本质上的作用是将当前user和privilige表中的用户信息/权限设置从mysql库(MySQL数据库的内置库)中提取到内存里
flush privileges;