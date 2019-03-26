	.data
sinp: .asciiz "enter > "
	.text
	.globl	main
main:
	# function main begin
	la $a0, sinp
	li $v0, 4
	syscall
	li $v0, 5
	syscall
	move $s0,$v0
	li $s1, 0
	# while loop: while less(0, inp)
wls0:
	li $t8, 0
	addi $sp, $sp, -4
	sw $t8, 0($sp)
	move $t9, $s0
	lw $t8, 0($sp)
	addi $sp, $sp, 4
	bge $t8, $t9, wle0
	move $t8, $s1
	move $t9, $s0
	add $s1, $t8, $t9
	move $t8, $s0
	li $t9, 1
	sub $s0, $t8, $t9
	j wls0
wle0:
	move $a0, $s1
	li $v0, 1
	syscall
	li $v0, 10
	syscall
