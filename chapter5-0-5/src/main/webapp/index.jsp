<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2017-03-10
  Time: 16:12
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>$Title$111</title>
</head>
<body>
<%--http://localhost:8505/gaga--%>
<form action="gaga"> <%--    method="post"  如果不写明表单的请求方式 那么默认是get请求--%>
    用户：<input type="text" name="USERNAME"><br>
    密码：<input type="password" name="PASSWORD"><br>
    兴趣：
    <input type="checkbox" name="INTEREST" value="篮球">篮球
    <input type="checkbox" name="INTEREST" value="足球">足球
    <input type="checkbox" name="INTEREST" value="水球">水球<br>
    性别：
    <input type="radio" name="SEX" value="1">男的
    <input type="radio" name="SEX" value="0">女的<br>
    籍贯：
    <select name="AREA">
        <option value ="1">山东</option>
        <option value ="2">辽宁</option>
        <option value="3">天津</option>
        <option value="4">陕西</option>
    </select>
    <br>
    <input type="submit" value="登录">
</form>


<div>
    <form action="shit" method="get" name="Form1" id="Form1">
        外号1： <input type="text" value="" name="nickname"/> <br>
        年龄1：  <input type="text" value="" name="age"/> <br>
        interesting:
        <input type="checkbox" name="interesting" value="reading"/>Reading
        <input type="checkbox" name="interesting" value="game"/>Game
        <input type="checkbox" name="interesting" value="party"/>Party
        <input type="checkbox" name="interesting" value="shopping"/>Shopping
        <input type="checkbox" name="interesting" value="sport"/>Sport
        <input type="checkbox" name="interesting" value="tv"/>TV

        <input type="submit" value="service测试"/>
    </form>
</div>


</body>
</html>
