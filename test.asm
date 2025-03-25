.data
sayHello: .asciiz "Hello new friend"
sayGoodbye: .asciiz "Goodbye for now"

.text
main:
    # R-type instructions
    add $t0, $t1, $t2         # $t0 = $t1 + $t2
    sub $s0, $s1, $s2         # $s0 = $s1 - $s2
    and $a0, $a1, $a2         # $a0 = $a1 & $a2
    or  $v0, $v1, $zero       # $v0 = $v1 | 0
    slt $t3, $t4, $t5         # $t3 = ($t4 < $t5) ? 1 : 0
    syscall                   # syscall

    # I-type instructions
    addiu $t1, $t2, 100       # $t1 = $t2 + 100
    andi  $s1, $s2, 255       # $s1 = $s2 & 0x00FF
    ori   $a1, $a2, 0x1234    # $a1 = $a2 | 0x1234
    beq   $t0, $t1, label1    # branch if equal
    bne   $t0, $t2, label2    # branch if not equal
    lui   $s2, 0x1234         # upper 16 bits = 0x1234
    lw    $t0, 0($sp)         # load word from stack pointer
    sw    $t1, 4($sp)         # store word to stack pointer + 4

    # J-type instruction
    j label3

    # Pseudo-instructions
label1:
    la $a0, sayHello          # load address of sayHello
    li $v0, 4                 # print syscall
    syscall

label2:
    move $t4, $t5             # $t4 = $t5
    blt $t4, $t5, label3      # if $t4 < $t5, jump to label3

label3:
    la $a0, sayGoodbye        # load address of sayGoodbye
    li $v0, 4                 # print syscall
    syscall

    li $v0, 10                # exit syscall
    syscall