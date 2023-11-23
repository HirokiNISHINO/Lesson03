; 64 bit code.
bits 64
	; to use the printf() and strcmp functions.
extern printf
extern strcmp
extern fmod

; data section.
section .data
	exit_fmt#:    db "exit code:%d", 10, 0 ; the format string for the exit message.

	print_int_fmt#:    db "%d", 0 ; the format string for the print int.
	print_string_fmt#:    db "%s", 0 ; the format string for the print string.
	print_double_fmt#:    db "%lf", 0 ; the format string for the print double.
	print_CR_fmt#:    db 10, 0 ; the format string for the print LF (\n).

	print_boolean_string_true#:    db "true", 0 ; the format string for the print double.
	print_boolean_string_false#:    db "false", 0 ; the format string for the print double.


; text section
section .text
	global _start ; the entry point.

; the subroutine for sys-exit. rax will be the exit code.
exit_program#:
	and rsp, 0xFFFFFFFFFFFFFFF0 ; stack must be 16 bytes aligned to call a C function.
	push rax ; we need to preserve rax here.
	push rax ; pushing twice for 16 byte alignment. We'll discard this later. 

	; call printf to print out the exit code.
	lea rdi, [rel exit_fmt#] ; the format string
	mov rsi, rax			; the exit code 
	mov rax, 0			; no xmm register is used.
	call printf

	pop rax ; this value will be discared (as we did 'push rax' twice for 16 bytes alignment.

	mov rax, 0x60; specify the exit sys call.
	pop rdi ; this is the rax value we pushed at the entry of this sub routine
	syscall ; exit!

; the function for print(int).
print_int#:
	push rbp 		; store the current base pointer.
	mov  rbp, rsp 	; move the base pointer to the new stack frame.
	and  rsp, 0xFFFFFFFFFFFFFFF0	; to make stack 16 byte aligned (ABI requires this!).

	lea  rdi, [rel print_int_fmt#]
	mov  rsi, rax
	mov  rax, 0
	call printf

	mov  rsp, rbp
	pop  rbp
	ret

; the function for print(string).
print_string#:
	push rbp 		; store the current base pointer.
	mov  rbp, rsp 	; move the base pointer to the new stack frame.
	and  rsp, 0xFFFFFFFFFFFFFFF0	; to make stack 16 byte aligned (ABI requires this!).

	lea  rdi, [rel print_string_fmt#]
	mov  rsi, rax
	mov  rax, 0
	call printf

	mov  rsp, rbp
	pop  rbp
	ret

; the function for print(double).
print_double#:
	push rbp 		; store the current base pointer.
	mov  rbp, rsp 	; move the base pointer to the new stack frame.
	and  rsp, 0xFFFFFFFFFFFFFFF0	; to make stack 16 byte aligned (ABI requires this!).

	lea  rdi, [rel print_double_fmt#]
	movq xmm0, rax
	mov  rax, 1
	call printf

	mov  rsp, rbp
	pop  rbp
	ret

; the function for print(boolean).
print_boolean#:
	push rbp 		; store the current base pointer.
	mov  rbp, rsp 	; move the base pointer to the new stack frame.
	and  rsp, 0xFFFFFFFFFFFFFFF0	; to make stack 16 byte aligned (ABI requires this!).

	cmp rax, 0
	je .print_boolean_false#

	.print_boolean_true#:
	lea rsi, [rel print_boolean_string_true#]
	jmp .print_boolean_print#

	.print_boolean_false#:
	lea rsi, [rel print_boolean_string_false#]

	.print_boolean_print#:
	lea rdi, [rel print_string_fmt#]
	mov rax, 0
	call printf

	mov  rsp, rbp
	pop  rbp
	ret

; the function for printCR.
print_CR#:
	push rbp 		; store the current base pointer.
	mov  rbp, rsp 	; move the base pointer to the new stack frame.
	and  rsp, 0xFFFFFFFFFFFFFFF0	; to make stack 16 byte aligned (ABI requires this!).

	lea  rdi, [rel print_CR_fmt#]
	mov  rax, 0
	call printf

	mov  rsp, rbp
	pop  rbp
	ret

_start:
	mov rax, 0 ; initialize the accumulator register.
	mov rax, 0
	cmp rax, 0
	jne cond_2_true
	mov rax, 1
	cmp rax, 0
	jne cond_2_true
cond_2_false:
	mov rax, 0
	jmp cond_2_end
cond_2_true:
	mov rax, 1
cond_2_end:

	jmp exit_program# ; exit the program, rax should hold the exit code.
