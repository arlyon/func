	.data
sinp: .asciiz "enter > "
	.text
	.globl	main
main:
	li $s1, 1
	li $s0, 0
	li $v0, 10
	syscall
