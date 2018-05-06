#include <jni.h>
#include <string.h>

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <netdb.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <sys/select.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>

#define send_MAXSIZE 50
#define recv_MAXSIZE 1024

struct NETBIOSNS {
	unsigned short int tid; //unsigned short int 占2字节
	unsigned short int flags;
	unsigned short int questions;
	unsigned short int answerRRS;
	unsigned short int authorityRRS;
	unsigned short int additionalRRS;
	unsigned char name[34];
	unsigned short int type;
	unsigned short int classe;
};

char *getNameFromIp(const char *ip);

extern "C"

jstring Java_com_example_mixture_WifiShareActivity_nameFromJNI(JNIEnv* env, jobject thiz, jstring ip) {
	const char* str_ip;
	str_ip = env->GetStringUTFChars(ip, 0);
	return env->NewStringUTF(getNameFromIp(str_ip));
}

char *getNameFromIp(const char *ip) {
	char str_info[1024] = { 0 };
	struct sockaddr_in toAddr; //sendto中使用的对方地址
	struct sockaddr_in fromAddr; //在recvfrom中使用的对方主机地址
	char send_buff[send_MAXSIZE];
	char recv_buff[recv_MAXSIZE];
	memset(send_buff, 0, sizeof(send_buff));
	memset(recv_buff, 0, sizeof(recv_buff));
	int sockfd; //socket
	unsigned int udp_port = 137;
	int inetat;
	if ((inetat = inet_aton(ip, &toAddr.sin_addr)) == 0) {
		sprintf(str_info, "[%s] is not a valid IP address\n", ip);
		return str_info;
	}
	if ((sockfd = socket(AF_INET, SOCK_DGRAM, IPPROTO_UDP)) < 0) {
		sprintf(str_info, "%s socket error sockfd=%d, inetat=%d\n", ip, sockfd, inetat);
		return str_info;
	}
	bzero((char*) &toAddr, sizeof(toAddr));
	toAddr.sin_family = AF_INET;
	toAddr.sin_addr.s_addr = inet_addr(ip);
	toAddr.sin_port = htons(udp_port);

	//构造netbios结构包
	struct NETBIOSNS nbns;
	nbns.tid = 0x0000;
	nbns.flags = 0x0000;
	nbns.questions = 0x0100;
	nbns.answerRRS = 0x0000;
	nbns.authorityRRS = 0x0000;
	nbns.additionalRRS = 0x0000;
	nbns.name[0] = 0x20;
	nbns.name[1] = 0x43;
	nbns.name[2] = 0x4b;
	int j = 0;
	for (j = 3; j < 34; j++) {
		nbns.name[j] = 0x41;
	}
	nbns.name[33] = 0x00;
	nbns.type = 0x2100;
	nbns.classe = 0x0100;
	memcpy(send_buff, &nbns, sizeof(nbns));
	int send_num = 0;
	send_num = sendto(sockfd, send_buff, sizeof(send_buff), 0,
			(struct sockaddr *) &toAddr, sizeof(toAddr));
	if (send_num != sizeof(send_buff)) {
		sprintf(str_info,
				"%s sendto() error sockfd=%d, send_num=%d, sizeof(send_buff)=%d\n",
				ip, sockfd, send_num, sizeof(send_buff));
		shutdown(sockfd, 2);
		return str_info;
	}
	int recv_num = recvfrom(sockfd, recv_buff, sizeof(recv_buff), 0,
			(struct sockaddr *) NULL, (socklen_t*) NULL);
	if (recv_num < 56) {
		sprintf(str_info, "%s recvfrom() error sockfd=%d, recv_num=%d\n", ip,
				sockfd, recv_num);
		shutdown(sockfd, 2);
		return str_info;
	}
	//这里要初始化。因为发现linux和模拟器都没问题，真机上该变量若不初始化，其值就不可预知
	unsigned short int NumberOfNames = 0;
	memcpy(&NumberOfNames, recv_buff + 56, 1);
	char str_name[1024] = { 0 };
	unsigned short int mac[6] = { 0 };
	int i = 0;
	for (i = 0; i < NumberOfNames; i++) {
		char NetbiosName[16];
		memcpy(NetbiosName, recv_buff + 57 + i * 18, 16);
		//依次读取netbios name
		if (i == 0) {
			sprintf(str_name, "%s", NetbiosName);
		}
	}
	sprintf(str_info, "%s|%s|", ip, str_name);
	for (i = 0; i < 6; i++) {
		memcpy(&mac[i], recv_buff + 57 + NumberOfNames * 18 + i, 1);
		sprintf(str_info, "%s%02X", str_info, mac[i]);
		if (i != 5) {
			sprintf(str_info, "%s-", str_info);
		}
	}
	return str_info;
}
