a = 0
b = 0
tmp = 0

print("введите а\n")
read("%d", a)
print("введите b\n")
read("%d", b)

while a != b do
    if a > b then
    tmp = a
    a = b
    b = tmp
    end
    b = b - a
end

print("НОД: %d\n", a)