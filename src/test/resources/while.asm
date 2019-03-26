	.data
sinp: .asciiz "enter > "
	.text
	.globl	main
main:
	li $s0, 0
	li $s1, 100
wls0:
	move $t8, $s0
	addi $sp, $sp, -4
	sw $t8, 0($sp)
	move $t9, $s1
	lw $t8, 0($sp)
	addi $sp, $sp, 4
	beq $t8, $t9, wle0
	li $s0, 100
	j wls0
wle0:
	li $v0, 10
	syscall
