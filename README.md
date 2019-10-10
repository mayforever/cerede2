# CeReDe 2.0
## Centralized Remote Desktop

**CeReDe** is opensource remote desktop. it`s like _**Team Viewer**_, _**AnyDesk**_, the different is you need costumize and provide your own server. base in what you need.

its 2.0 because I used RMI to speed up and minimized consumed data. for old version
[Cerede 1](https://github.com/mayforever/cerede).

**ceredeserver** is the serverside of this project. it handled username, password, address, and command of every client.

Requirements :
* optional: port forwading certain ip. if you want to use it in wan
* configure **"conf/ceredeserver.conf.xml"**. 

```
<?xml version="1.0" encoding="UTF-8"?>

<!DOCTYPE beans PUBLIC "-//SPRING//DTD BEAN//EN"
"http://www.springframework.org/dtd/spring-beans.dtd">

<beans>
    <bean id = "ceredeserver" class = "com.mayforever.ceredeserver.conf.Configuration">
        <property name="filePath" value = "/home/mis/eclipse-workspace/ceredeserver/"/>
	<property name="processorCount" value = "4"/>
	<property name="port" value = "4452"/>
	<property name="address" value = "192.168.0.23"/>
	<property name="rmiPort" value = "4453"/>

    </bean>
</beans>

```

**ceredeclient** this is the client side that comunicate to the server. this need to install in every pc we want to control or use to control. 

Requirement
* configure **"conf/ceredeclient.conf.xml"**. 

```
<?xml version="1.0" encoding="UTF-8"?>

<!DOCTYPE beans PUBLIC "-//SPRING//DTD BEAN//EN"
"http://www.springframework.org/dtd/spring-beans.dtd">

<beans>
    <bean id = "ceredeclient" class = "com.mayforever.ceredeclient.conf.Configuration">
        <property name="filePath" value = "/home/mis/NetBeansProjects/ceredeclient/"/>
	<property name="rmiServerPort" value = "4453"/>
	<property name="serverPort" value = "4452"/>
	<property name="serverAddress" value = "192.168.0.23"/>
	<property name="username" value = "aaron15"/>
	<property name="password" value = "aaronpassword"/>
    </bean>
</beans>

```

for now. it support viewing and controlling other pc. it not support clipboard and filetransfer, but next time I add on it. I add those both feature. 

Sorry for my bad english. I hope you understand, thanks.

ps: this is not final. I need to improve it. so I open in any suggestion. 

sorry for not giving the bin files yet. This is undercontruction. pls wait patiently. but you can compile it if you want.just clone the following library.[Java Tools](https://github.com/mayforever/JavaTools) (pick alwayslatest version). thanks
