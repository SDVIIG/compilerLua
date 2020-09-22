a = "acaa"
b = "bcaa"

print("Строка  '%s'", a)
print(" содержит строку '%s'?\n", b)

c = strstr(a,b)

if c == "null" then
    print("false\n") else
    print("true\n")
end