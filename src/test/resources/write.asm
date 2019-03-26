	.data
sinp: .asciiz "enter > "
	.text
	.globl	main
main:
	li $a0, 1
	li $v0, 1
	syscall
	li $v0, 10
	syscall
