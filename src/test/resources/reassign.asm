	.data
sinp: .asciiz "enter > "
	.text
	.globl	main
main:
	# function main begin
	li $s1, 1
	move $s0, $s1
	li $v0, 10
	syscall
