	.data
sinp: .asciiz "enter > "
	.text
	.globl	main
main:
	li $v0, 4
	la $a0, sinp
	syscall
	li $v0, 5
	syscall
	move $s0,$v0
	li $v0, 10
	syscall
