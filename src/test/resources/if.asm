	.data
sinp: .asciiz "enter > "
	.text
	.globl	main
main:
	# function main begin
	li $s0, 0
	li $s1, 1
	# if statement: if eq(x, y)
	move $t8, $s0
	addi $sp, $sp, -4
	sw $t8, 0($sp)
	move $t9, $s1
	lw $t8, 0($sp)
	addi $sp, $sp, 4
	beq $t8, $t9, ift0
	move $s0, $s1
	j ife0
ift0:
	li $s0, 100
ife0:
	li $v0, 10
	syscall
