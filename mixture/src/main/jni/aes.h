#ifndef AES_H
#define AES_H

#include <string.h>

class AES
{
public:
  AES(unsigned char* key);
  virtual ~AES();
  unsigned char* Cipher(unsigned char* input);
  unsigned char* InvCipher(unsigned char* input);
  void* Cipher(void* input, int length=0);
  void* InvCipher(void* input, int length);

  void Cipher(char *input, char *output);
  void InvCipher(char *inut, char *output);

private:
  unsigned char Sbox[256];
  unsigned char InvSbox[256];
  unsigned char ww[11][4][4];

  void KeyExpansion(unsigned char* key, unsigned char w[][4][4]);
  unsigned char FFmul(unsigned char a, unsigned char b);

  void SubBytes(unsigned char state[][4]);
  void ShiftRows(unsigned char state[][4]);
  void MixColumns(unsigned char state[][4]);
  void AddRoundKey(unsigned char state[][4], unsigned char k[][4]);

  void InvSubBytes(unsigned char state[][4]);
  void InvShiftRows(unsigned char state[][4]);
  void InvMixColumns(unsigned char state[][4]);

  int strToHex(const char *ch, char *hex);
  int hexToStr(const char *hex, char *ch);
  int ascillToValue(const char ch);
  char valueToHexCh(const int value);
  int getUCharLen(const unsigned char *uch);
  int strToUChar(const char *ch, unsigned char *uch);
  int ucharToStr(const unsigned char *uch, char *ch);
  int ucharToHex(const unsigned char *uch, char *hex);
  int hexToUChar(const char *hex, unsigned char *uch);
};

#endif // AES_H

