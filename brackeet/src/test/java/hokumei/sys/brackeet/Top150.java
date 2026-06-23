package hokumei.sys.brackeet;

import java.util.*;

public class Top150 {

	public int calculate(String s) {

		if (s == null) return 0;
		int length = s.length();
		if (length == 0) return 0;
		int number = 0;
		Deque<Integer> stack = new ArrayDeque<>();
		int result = 0;
		int sign = 1;
		for (int i = 0; i < length; i++) {
			char c = s.charAt(i);
			if (Character.isDigit(c)) {
				number = number * 10 + c - '0';
			} else if (c == '+') {
				result += sign * number;
				number = 0;
				sign = 1;
			} else if (c == '-') {
				result += sign * number;
				number = 0;
				sign = -1;
			} else if (c == '(') {
				stack.push(result);
				stack.push(sign);
				result = 0;
				sign = 1;
			} else if (c == ')') {
				result += sign * number;
				result *= stack.pop();
				result += stack.pop();
				number = 0;
			}
		}
		return result + sign * number;
	}

	public ListNode reverseKGroup(ListNode head, int k) {
		ListNode dummy = new ListNode(0);
		dummy.next = head;

		ListNode groupPrev = dummy;
//
//		while (true) {
//			ListNode kth = getKth(groupPrev, k);
//			if (kth == null) break;
//
//			ListNode groupNext = kth.next;
//
//			// 反转
//			ListNode prev = groupNext;
//			ListNode curr = groupPrev.next;
//
//			while (curr != groupNext) {
//				ListNode tmp = curr.next;
//				curr.next = prev;
//				prev = curr;
//				curr = tmp;
//			}
//
//			// 接回去
//			ListNode tmp = groupPrev.next;
//			groupPrev.next = kth;
//			groupPrev = tmp;
//		}
//
//		return dummy.next;
		while (true) {
			ListNode kthNode = getKth(groupPrev, k);
			if (kthNode == null) break;
			ListNode groupNext = kthNode.next;
			ListNode prev = groupNext;
			ListNode current = groupPrev.next;
			while (current != kthNode.next) {
				ListNode temp = current.next;
				current.next = prev;
				prev = current;
				current = temp;
			}
			ListNode tmp = groupPrev.next;
			groupPrev.next = kthNode;
			groupPrev = tmp;
		}
		return dummy.next;

	}

	public ListNode getKth(ListNode head, int k) {
		if (head == null || k <= 0) return null;
		while (k >= 0 && head.next != null) {
			head = head.next;
			k--;
		}
		return head;
	}

	static class TreeNode {
		int val;
		TreeNode left;
		TreeNode right;

		TreeNode(int x) {
			val = x;
		}
	}

	public void flatten(TreeNode root) {
		flatternTreeNode(root);
	}

	public TreeNode flatternTreeNode(TreeNode root) {
		if (root == null) return null;
		TreeNode left = flatternTreeNode(root.left);
		TreeNode right = flatternTreeNode(root.right);
		if (left != null) {
			left.right = root.right; //接原来的右子树
			root.right = root.left; //左子树变成了右子树
			root.left = null;
		}
		if (right != null) {
			return right;
		}
		if (left != null) {
			return left;
		}
		return root;
	}

	public double findMedianSortedArrays(int[] nums1, int[] nums2) {
		if (nums1 == null) return getMedian(nums2);
		if (nums2 == null) return getMedian(nums1);
		int nums1Len = nums1.length;
		int nums2Len = nums2.length;
		if (nums1Len == 0) {
			return getMedian(nums2);
		}
		if (nums2Len == 0) {
			return getMedian(nums1);
		}

		int mod = (nums1Len + nums2Len) & 1;
		if (nums2Len < nums1Len) {
			return findMedianSortedArrays(nums2, nums1);
		}
		int left = 0;
		int right = nums1Len;
		while (left <= right) {
			int i = (left + right) / 2;
			int j = (nums1Len + nums2Len + 1) / 2 - i;
			int nums1Left = (i == 0) ? Integer.MIN_VALUE : nums1[i - 1];
			int nums2Left = (j == 0) ? Integer.MIN_VALUE : nums2[j - 1];
			int nums1Right = i == nums1Len ? Integer.MAX_VALUE : nums1[i];
			int nums2Right = j == nums2Len ? Integer.MAX_VALUE : nums2[j];
			if (nums1Left <= nums2Right && nums2Left <= nums1Right) {
				if (mod == 1) {
					return (double) Math.max(nums1Left, nums2Left);
				} else {
					return (Math.max(nums1Left, nums2Left) + Math.min(nums1Right, nums2Right)) / 2.0;
				}
			} else if (nums1Left > nums2Right) {
				right = i - 1;
			} else {
				left = i + 1;
			}
		}
		return 0.0;
	}

	double getMedian(int[] nums) {
		if (nums == null || nums.length == 0) return 0.0;
		int length = nums.length;
		int mid = length / 2;
		int mod = length % 2;
		if (mod == 1) return nums[mid];
		return (nums[mid - 1] + nums[mid]) / 2.0;
	}

	public ListNode mergeKLists(ListNode[] lists) {
		if (lists == null || lists.length == 0) return null;
		PriorityQueue<ListNode> queue = new PriorityQueue<>((l1, l2) -> l1.val - l2.val);
		ListNode dummyHead = new ListNode(0);
		ListNode current = dummyHead;
		int k = lists.length;
		for (ListNode node : lists) {
			if (node != null) {
				queue.offer(node);
			}
		}
		while (!queue.isEmpty()) {
			ListNode node = queue.poll();
			current.next = node;
			current = node;
			if (current.next != null) {
				queue.offer(current.next);
			}
		}
		return dummyHead.next;
	}

	public int searchInsert(int[] nums, int target) {
		if (nums == null) return -1;
		if (nums.length == 0) return 0;
		int left = 0;
		int right = nums.length - 1;
		while (left <= right) {
			int mid = (left + right) / 2;
			if (nums[mid] > target) {
				right = mid - 1;
			} else {
				left = mid + 1;
			}
		}
		return left;
	}

	public double myPow(double x, int n) {
		long pow = (long) n;
		if (pow < 0) {
			pow = -pow;
			x = 1 / x;
		}
		double result = 1;
		while (pow > 0) {
			if ((pow & 1) == 1) result *= x;
			x = x * x;
			pow >>= 1;
		}
		return result;
	}

	 List<String> resultList = new ArrayList<String>();

	public  String getPermutation(int n, int k) {
		backTracking(n, new StringBuilder(), new boolean[n + 1]);
		return resultList.get(k - 1);
	}

	public  void backTracking(int n, StringBuilder builder, boolean[] visited) {
		if (builder.length() == n) {
			resultList.add(builder.toString());
			return;
		}
		for (int i = 1; i <= n; i++) {
			if (visited[i]) continue;
			visited[i] = true;
			builder.append(i);
			backTracking(n, builder, visited);
			builder.deleteCharAt(builder.length() - 1);
			visited[i] = false;
		}
	}

	public static void main(String[] args) {
		System.out.println(new Top150().getPermutation(3, 3));
	}
}


