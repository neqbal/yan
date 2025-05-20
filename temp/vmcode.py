import gdb

class ReadMemoryCommand(gdb.Command):
    def __init__(self):
        super(ReadMemoryCommand, self).__init__("read_memory", gdb.COMMAND_USER)

    def invoke(self, arg, from_tty):
        try:
            args = gdb.string_to_argv(arg)
            if len(args) != 3:
                print("Usage: read_memory ADDRESS LENGTH FILENAME")
                return

            address = int(gdb.parse_and_eval(args[0]))
            length = int(gdb.parse_and_eval(args[1]))
            filename = args[2]

            with open(filename, "wb") as f:
                for i in range(length):
                    result = gdb.execute(f"x/1xb {address + i}", to_string=True).strip()
                    byte_str = result.split(":")[1].strip().split()[0]
                    byte_val = int(byte_str, 16)
                    f.write(byte_val.to_bytes(1, byteorder='little'))

            print(f"Wrote {length} bytes from {hex(address)} to {filename}")

        except Exception as e:
            print(f"Error: {e}")

ReadMemoryCommand()
