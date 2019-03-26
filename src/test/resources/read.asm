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
	li $v0, 10
	syscall
