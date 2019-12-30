#ifndef NATIVE_LIB_H
#define NATIVE_LIB_H

#include <jni.h>
#include <string>
#include <exception>
#include <stdexcept>

#include "BigIntegerLibrary.h"

struct numbersystem
{

    const std::string number;
    const int base;

    numbersystem(const int base, const std::string& number)
            : base(base), number(number)
    {

    }

    const int charToInt(const char c) const
    {
        return (int)(c - '0');
    }

    int evaluate(const char c) const
    {
        if (c >= '0' && c <= '9')
            return charToInt(c);
        else if (c >= 'A' && c <= 'Z')
            return 10 + charToInt(c) - charToInt('A');
        else if (c >= 'a' && c <= 'z')
            return 10 + 26 + charToInt(c) - charToInt('a');
        else if (c == '+')
            return 62;
        else if (c == '-')
            return 63;
        else
            throw std::exception();
    }

    char reval(const int num) const
    {
        const static std::string base_digits = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz+-";
        return base_digits[num];
    }

    const BigInteger toDeci(const std::string& str, const int base) const
    {
        const auto len = str.length();
        const auto bbase = BigInteger(base);
        auto power = stringToBigInteger("1");
        auto num = stringToBigInteger("0");
        int i = len - 1;
        while (i >= 0)
        {
            if (evaluate(str[i]) >= base)
                throw std::exception();

            const auto q = evaluate(str[i]);
            const auto u = stringToBigInteger(std::to_string(q));
            const auto r = u * power;
            num = num + r;
            power = power * bbase;
            --i;
        }
        return num;
    }

    std::string fromDeci(const int base1, BigInteger& inputNum) const
    {
        std::string s = "";
        const auto bbase1 = stringToBigInteger(std::to_string(base1));
        const auto zero = BigInteger();

        while (inputNum > zero)
        {
            const auto rem = inputNum % bbase1;

            s += reval(std::stoi(bigIntegerToString(rem)));
            inputNum = inputNum / bbase1;
        }

        std::reverse(s.begin(), s.end());
        return s;
    }

    std::string convert(const int baseto) const
    {
        auto dec = toDeci(number, base);
        if (baseto == 10)
            return bigIntegerToString(dec);
        return fromDeci(baseto, dec);
    }
};

extern "C" JNIEXPORT jstring JNICALL Java_de_phil_solidsabissupershinysammlung_database_NativeNumberConverter_convertNumber(JNIEnv* env, jobject thiz, jint baseFrom, jint baseTo, jstring number) {

    const char* chars = env->GetStringUTFChars(number, nullptr);
    std::string num(chars);
    numbersystem ns(baseFrom, num);
    const std::string result = ns.convert(baseTo);

    env->ReleaseStringUTFChars(number, chars);

    return env->NewStringUTF(result.c_str());
}

#endif // NATIVE_LIB_H