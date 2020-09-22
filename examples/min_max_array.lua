i = 0
a = {53, 12, 33, 17, 28, 31, 27, 3, 6, 8}
n = 10
min = a[0]
max = a[0]

while i < n do
    if a[i] < min then
        min = a[i]
        end
    i = i + 1
end

i = 0

while i < n do
    if a[i] > max then
        max = a[i]
    end
    i = i + 1
end

print("\n Минимальный элемент %d\n", min)
print("\n Максимальный элемент %d\n", max)