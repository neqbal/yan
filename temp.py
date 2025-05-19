out = []
with open("temp", "r") as f:
    a = f.readlines()
    for i in a:
        i = i.strip().split("  ")
        out.append((int(i[2]) - int(i[0], 16)) & 0xFF)

print(''.join(f'\\x{byte:02x}' for byte in out))
